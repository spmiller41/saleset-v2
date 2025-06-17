package com.saleset.integration.zoho.service;

import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.core.entities.Address;
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
                logger.debug("Update Lead Response body: {}", response.getBody());
            }
        } catch (RestClientException ex) {
            logger.error("Appointment unable to be added to Lead: {} -- Message: {}", lead.getId(), ex.getMessage());
        }
    }




    /**
     * Updates the specified Lead in Zoho CRM with a new appointment date/time, sales manager owner, and address details.
     *
     * @param appointment the Appointment object containing the start date/time to set on the lead
     * @param address     the Address object containing street, city, state, and zip to update on the lead
     * @param zcrmLeadId  the Zoho CRM ID of the lead to update
     */
    public void updateLeadAppointment(Appointment appointment, Address address, String zcrmLeadId) {
        String accessToken = tokenService.getAccessToken(ZohoModuleApiName.LEADS);
        JSONObject requestBody = ZohoPayloadUtil.buildAppointmentPayload(appointment, address, zcrmSalesManagerId);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    ZohoUtils.buildEndpoint(zcrmApiBaseUrl, zcrmLeadId),
                    HttpMethod.PUT,
                    new HttpEntity<>(requestBody.toString(), ZohoUtils.buildHeaders(accessToken)),
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Appointment & Address added to Lead ID: {} -- Status Code: {}", zcrmLeadId, response.getStatusCode());
            } else {
                logger.warn("Unexpected status updating Lead & Address {} -- {}", zcrmLeadId, response.getStatusCode());
                logger.debug("Update Lead & Address Response body: {}", response.getBody());
            }
        } catch (RestClientException ex) {
            logger.error("Appointment & Address unable to be added to Lead: {} -- Message: {}", zcrmLeadId, ex.getMessage());
        }
    }




    /**
     * Creates a new Lead in Zoho CRM using the provided appointment data.
     * <p>
     * Constructs the JSON payload, sends a POST request to the Zoho Leads endpoint,
     * and wraps the raw JSON response in a ZohoLeadCreateUpdateResponse for further inspection.
     * Logs an INFO on successful (2xx) creation, WARN on unexpected status codes,
     * and captures error responses for parsing.
     *
     * @param appointmentData the appointment details to use when creating the Lead record
     * @return a ZohoLeadCreateUpdateResponse containing the Zoho response code and record ID
     */
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




    /**
     * Fetches a Lead record from Zoho CRM by its Zoho ID.
     * <p>
     * Sends a GET to the Zoho Leads endpoint and parses the response body
     * into a ZohoLeadFetchResponse. Returns Optional.empty() if the call fails
     * or returns a non-2xx status.
     *
     * @param zcrmLeadId the Zoho CRM ID of the Lead to fetch
     * @return an Optional containing the ZohoLeadFetchResponse if successful, otherwise Optional.empty()
     */
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
