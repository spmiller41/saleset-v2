package com.saleset.integration.zoho.service;

import com.saleset.core.entities.Appointment;
import com.saleset.core.entities.Lead;
import com.saleset.integration.zoho.enums.ZohoModuleApiName;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class ZohoLeadsService implements ZohoUtility {

    private static final Logger logger = LoggerFactory.getLogger(ZohoLeadsService.class);

    @Value("${ambassador.name}")
    private String ambassadorName;

    @Value("${zcrm.api.base.url}")
    private String zcrmApiBaseUrl;

    @Value("${zcrm.create.sub.source}")
    private String subSource;

    @Value("${zcrm.create.lead.source}")
    private String leadSource;

    @Value("${zcrm.lead.sales.manager.id}")
    private String salesManagerId;

    private final RestTemplate restTemplate;
    private final ZohoCrmTokenService tokenService;

    @Autowired
    public ZohoLeadsService(RestTemplate restTemplate, ZohoCrmTokenService tokenService) {
        this.restTemplate = restTemplate;
        this.tokenService = tokenService;
    }

    public void updateLeadAppointment(Appointment appointment, Lead lead) {
        String accessToken = tokenService.getAccessToken(ZohoModuleApiName.LEADS);
        String zcrmLeadId = lead.getZcrmExternalId();
        LocalDateTime startsAt = appointment.getStartDateTime();

        String endpoint = String.format("%s%s/%s", zcrmApiBaseUrl, ZohoModuleApiName.LEADS, zcrmLeadId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        JSONObject updatedFields = new JSONObject();
        updatedFields.put("Appointment", formatDateTime(startsAt));
        updatedFields.put("Owner", salesManagerId);
    }
}
