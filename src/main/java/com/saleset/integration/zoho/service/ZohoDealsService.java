package com.saleset.integration.zoho.service;

import com.saleset.core.entities.Address;
import com.saleset.core.entities.Appointment;
import com.saleset.integration.zoho.dto.response.ZohoFetchResponse;
import com.saleset.integration.zoho.enums.ZohoModuleApiName;
import com.saleset.integration.zoho.util.ZohoPayloadUtil;
import com.saleset.integration.zoho.util.ZohoUtils;
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

    public Optional<ZohoFetchResponse> fetchLead(String field, String value) {
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
            if (!resp.getStatusCode().is2xxSuccessful()) {
                logger.warn("Unexpected status fetching Deal from Zoho CRM. Field: {} - Value: {}. Body: {}",
                        field, value, body);
                return Optional.empty();
            }

            return Optional.of(new ZohoFetchResponse(body));
        } catch (RestClientException e) {
            logger.warn("Error fetching Deal from Zoho CRM. Field: {} - Value: {} --- Message: {}",
                    field, value, e.getMessage());
            return Optional.empty();
        }
    }

    public void updateLeadAppointment(Appointment appointment, Address address, ZohoFetchResponse fetchResponse) {
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
