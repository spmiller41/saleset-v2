package com.saleset.integration.zoho.service;

import com.saleset.core.entities.Appointment;
import com.saleset.core.entities.Lead;
import com.saleset.integration.zoho.constants.ZohoLeadFields;
import com.saleset.integration.zoho.enums.ZohoModuleApiName;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ZohoLeadsService {

    private static final Logger logger = LoggerFactory.getLogger(ZohoLeadsService.class);

    @Value("${ambassador.name}")
    private String ambassadorName;

    @Value("${zcrm.api.base.url}")
    private String zcrmApiBaseUrl;

    @Value("${zcrm.create.sub.source}")
    private String zcrmSubSource;

    @Value("${zcrm.create.lead.source}")
    private String zcrmLeadSource;

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

        JSONObject updatedFields = new JSONObject();
        updatedFields.put(ZohoLeadFields.APPOINTMENT, ZohoUtils.formatDateTime(appointment.getStartDateTime()));
        updatedFields.put(ZohoLeadFields.OWNER, zcrmSalesManagerId);

        JSONObject requestBody = new JSONObject();
        requestBody.put(ZohoLeadFields.DATA, new JSONArray(updatedFields));

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

}
