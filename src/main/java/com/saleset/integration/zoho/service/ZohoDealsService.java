package com.saleset.integration.zoho.service;

import com.saleset.core.entities.Address;
import com.saleset.core.entities.Appointment;
import com.saleset.integration.zoho.dto.response.ZohoFetchResponse;
import com.saleset.integration.zoho.enums.ZohoModuleApiName;
import com.saleset.integration.zoho.util.ZohoPayloadUtil;
import com.saleset.integration.zoho.util.ZohoUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ZohoDealsService {

    private static final Logger logger = LoggerFactory.getLogger(ZohoDealsService.class);

    @Value("${ambassador.name}")
    private String ambassadorName;

    @Value("${zcrm.api.base.url}")
    private String zcrmApiBaseUrl;

    @Value("${zcrm.lead.sales.manager.id}")
    private String zcrmSalesManagerId;

    private final RestTemplate restTemplate;
    private final ZohoCrmTokenService tokenService;

    @Autowired
    public ZohoDealsService(RestTemplate restTemplate, ZohoCrmTokenService tokenService) {
        this.restTemplate = restTemplate;
        this.tokenService = tokenService;
    }


    /**
     * Searches the Zoho CRM Deals module for a record matching the given field and value.
     * <p>
     * This method:
     * <ol>
     *   <li>Retrieves an access token for the Deals module.</li>
     *   <li>Builds the search URL using the specified field and value.</li>
     *   <li>Makes an HTTP GET request to Zoho.</li>
     *   <li>If the response is not 2xx, or the body is null/blank, logs an info message and returns empty.</li>
     *   <li>Parses the JSON body; if the "data" array is missing or empty, logs an info message and returns empty.</li>
     *   <li>Otherwise constructs and returns a {@link ZohoFetchResponse} wrapped in an Optional.</li>
     * </ol>
     *
     * @param field the Zoho API field name to search (e.g. "Auto_Number_1")
     * @param value the value to match for the given field
     * @return an Optional containing the fetch response if a matching deal is found, or Optional.empty() otherwise
     */
    public Optional<ZohoFetchResponse> fetchDeal(String field, String value) {
        String token = tokenService.getAccessToken(ZohoModuleApiName.DEALS);
        String url   = ZohoUtils.buildSearchRecordEndpoint(zcrmApiBaseUrl, field, value, ZohoModuleApiName.DEALS);

        try {
            ResponseEntity<String> resp = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(ZohoUtils.buildHeaders(token)),
                    String.class
            );

            String body = resp.getBody();

            if (!resp.getStatusCode().is2xxSuccessful() || body == null || body.isBlank()) {
                logger.info("No response body fetching Deal {} = {}; status was {}", field, value, resp.getStatusCode());
                return Optional.empty();
            }

            JSONObject root = new JSONObject(body);
            JSONArray data = root.optJSONArray("data");
            if (data == null || data.isEmpty()) {
                logger.info("No matching Deal found for {} = {}", field, value);
                return Optional.empty();
            }

            return Optional.of(new ZohoFetchResponse(body));

        } catch (RestClientException e) {
            logger.warn("Error fetching Deal {} = {}: {}", field, value, e.getMessage());
            return Optional.empty();
        }
    }


    public void updateDealAppointment(Appointment appointment, Address address, ZohoFetchResponse fetchResponse) {
        String zcrmDealId = fetchResponse.getId();
        String accessToken = tokenService.getAccessToken(ZohoModuleApiName.DEALS);
        JSONObject requestBody = ZohoPayloadUtil.buildAppointmentPayload(appointment, address, zcrmSalesManagerId);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    ZohoUtils.buildEndpoint(zcrmApiBaseUrl, zcrmDealId, ZohoModuleApiName.DEALS),
                    HttpMethod.PUT,
                    new HttpEntity<>(requestBody.toString(), ZohoUtils.buildHeaders(accessToken)),
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Appointment & Address added to Deal ID: {} -- Status Code: {}", zcrmDealId, response.getStatusCode());
            } else {
                logger.warn("Unexpected status updating Deal & Address {} -- {}", zcrmDealId, response.getStatusCode());
                logger.debug("Update Deal & Address Response body: {}", response.getBody());
            }
        } catch (RestClientException ex) {
            logger.error("Appointment & Address unable to be added to Deal: {} -- Message: {}", zcrmDealId, ex.getMessage());
        }
    }

}
