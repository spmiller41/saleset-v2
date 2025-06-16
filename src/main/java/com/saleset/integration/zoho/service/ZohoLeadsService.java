package com.saleset.integration.zoho.service;

import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.core.entities.Appointment;
import com.saleset.core.entities.Lead;
import com.saleset.integration.zoho.dto.response.ZohoLeadCreateUpdateResponse;
import com.saleset.integration.zoho.dto.response.ZohoLeadFetchResponse;
import com.saleset.integration.zoho.enums.ZohoModuleApiName;
import com.saleset.integration.zoho.util.ZohoPayloadUtil;
import com.saleset.integration.zoho.util.ZohoUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ZohoLeadsService {

    private static final Logger logger = LoggerFactory.getLogger(ZohoLeadsService.class);

    @Value("${ambassador.name}")
    private String ambassadorName;

    @Value("${zcrm.api.base.url}")
    private String zcrmApiBaseUrl;

    @Value("${zcrm.lead.sales.manager.id}")
    private String zcrmSalesManagerId;

    private final RestTemplate restTemplate;
    private final ZohoCrmTokenService tokenService;

    @Autowired
    public ZohoLeadsService(RestTemplate restTemplate, ZohoCrmTokenService tokenService) {
        this.restTemplate = restTemplate;
        this.tokenService = tokenService;
    }




    /**
     * Updates the specified Lead record in Zoho CRM by setting its appointment date and owner.
     *
     * @param appointment the Appointment containing the start date/time to set on the lead
     * @param lead        the Lead entity whose external Zoho ID will be updated
     */
    public void updateLeadAppointment(Appointment appointment, Lead lead) {
        String accessToken = tokenService.getAccessToken(ZohoModuleApiName.LEADS);
        JSONObject requestBody = ZohoPayloadUtil.buildAppointmentPayload(appointment, zcrmSalesManagerId);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    ZohoUtils.buildEndpoint(zcrmApiBaseUrl, lead.getZcrmExternalId()),
                    HttpMethod.PUT,
                    new HttpEntity<>(requestBody.toString(), ZohoUtils.buildHeaders(accessToken)),
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Appointment added to Lead ID: {} -- Status Code: {}", lead.getId(), response.getStatusCode());
            } else {
                logger.warn("Unexpected status updating Lead {} -- {}", lead.getId(), response.getStatusCode());
                logger.debug("Response body: {}", response.getBody());
            }
        } catch (RestClientException ex) {
            logger.error("Appointment unable to be added to Lead: {} -- Message: {}", lead.getId(), ex.getMessage());
        }
    }




    public ZohoLeadCreateUpdateResponse createLead(AppointmentRequest appointmentData) {
        String accessToken = tokenService.getAccessToken(ZohoModuleApiName.LEADS);
        JSONObject requestBody = ZohoPayloadUtil.buildLeadCreatePayload(
                appointmentData, zcrmSalesManagerId, ambassadorName
        );

        String responseBody;
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    ZohoUtils.buildEndpoint(zcrmApiBaseUrl),
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody.toString(), ZohoUtils.buildHeaders(accessToken)),
                    String.class
            );

            responseBody = response.getBody();

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Lead created with Appointment: {}", appointmentData);
            } else {
                logger.warn(
                        "Unexpected status creating Lead with Appointment: {} -- {}",
                        appointmentData, response.getStatusCode()
                );
                logger.debug("Response Body: {}", responseBody);
            }

            logger.info("Response: {}", response);
        } catch (HttpClientErrorException e) {
            responseBody = e.getResponseBodyAsString();
            logger.warn(
                    "HttpClient - Lead unable to be created with Appointment: {} -- Message: {}",
                    appointmentData, e.getMessage()
            );
        } catch (RestClientException ex) {
            responseBody = "{}";
            logger.warn(
                    "RestClient - Lead unable to be created with Appointment: {} -- Message: {}",
                    appointmentData, ex.getMessage()
            );
        }

        return new ZohoLeadCreateUpdateResponse(responseBody);
    }




    public Optional<ZohoLeadFetchResponse> fetchLead(String zcrmLeadId) {
        String token = tokenService.getAccessToken(ZohoModuleApiName.LEADS);
        String url   = ZohoUtils.buildEndpoint(zcrmApiBaseUrl, zcrmLeadId);

        try {
            ResponseEntity<String> resp = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(ZohoUtils.buildHeaders(token)),
                    String.class
            );

            String body = resp.getBody();
            if (!resp.getStatusCode().is2xxSuccessful()) {
                logger.warn("Unexpected status fetching Lead {} → {}. Body: {}",
                        zcrmLeadId, resp.getStatusCode(), body);
                return Optional.empty();
            }

            return Optional.of(new ZohoLeadFetchResponse(body));
        } catch (RestClientException e) {
            logger.warn("Error fetching Lead {} from Zoho CRM: {}", zcrmLeadId, e.getMessage());
            return Optional.empty();
        }
    }

}
