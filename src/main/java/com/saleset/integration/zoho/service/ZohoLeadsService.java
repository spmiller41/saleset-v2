package com.saleset.integration.zoho.service;

import com.saleset.core.dto.request.AppointmentRequest;
import com.saleset.core.entities.Address;
import com.saleset.core.entities.Appointment;
import com.saleset.core.entities.Lead;
import com.saleset.integration.zoho.dto.response.ZohoFetchResponse;
import com.saleset.integration.zoho.dto.response.ZohoLeadUpsertResponse;
import com.saleset.integration.zoho.enums.ZohoModuleApiName;
import com.saleset.integration.zoho.util.ZohoPayloadUtil;
import com.saleset.integration.zoho.util.ZohoUtils;
import org.json.JSONArray;
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
    public ZohoLeadUpsertResponse updateLeadAppointment(Appointment appointment, Lead lead) {
        String accessToken = tokenService.getAccessToken(ZohoModuleApiName.LEADS);
        JSONObject requestBody =
                ZohoPayloadUtil.buildAppointmentPayload(appointment, zcrmSalesManagerId, ZohoModuleApiName.LEADS);

        String responseBody;
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    ZohoUtils.buildEndpoint(zcrmApiBaseUrl, lead.getZcrmExternalId(), ZohoModuleApiName.LEADS),
                    HttpMethod.PUT,
                    new HttpEntity<>(requestBody.toString(), ZohoUtils.buildHeaders(accessToken)),
                    String.class);

            responseBody = response.getBody();

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Appointment added to Lead ID: {} -- Status Code: {}", lead.getId(), response.getStatusCode());
            } else {
                logger.warn("Unexpected status updating Lead {} -- {}", lead.getId(), response.getStatusCode());
                logger.debug("Update Lead Response body: {}", response.getBody());
            }
        } catch (HttpClientErrorException e) {
            responseBody = e.getResponseBodyAsString();
            logger.error(
                    "HttpClientErrorException - Appointment unable to be added to Lead: {} -- Message: {}",
                    lead.getId(), e.getMessage());
        } catch (RestClientException ex) {
            responseBody = "{}";
            logger.error(
                    "RestClientException - Appointment unable to be added to Lead: {} -- Message: {}",
                    lead.getId(), ex.getMessage());
        }

        return new ZohoLeadUpsertResponse(responseBody);
    }




    /**
     * Updates the specified Lead in Zoho CRM with a new appointment date/time, sales manager owner, and address details.
     *
     * @param appointment the Appointment object containing the start date/time to set on the lead
     * @param address     the Address object containing street, city, state, and zip to update on the lead
     * @param zcrmLeadId  the Zoho CRM ID of the lead to update
     */
    public ZohoLeadUpsertResponse updateLeadAppointment(Appointment appointment, Address address, String zcrmLeadId) {
        String accessToken = tokenService.getAccessToken(ZohoModuleApiName.LEADS);
        JSONObject requestBody =
                ZohoPayloadUtil.buildAppointmentPayload(appointment, address, zcrmSalesManagerId, ZohoModuleApiName.LEADS);

        String responseBody;
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    ZohoUtils.buildEndpoint(zcrmApiBaseUrl, zcrmLeadId, ZohoModuleApiName.LEADS),
                    HttpMethod.PUT,
                    new HttpEntity<>(requestBody.toString(), ZohoUtils.buildHeaders(accessToken)),
                    String.class);

            responseBody = response.getBody();

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Appointment & Address added to Lead ID: {} -- Status Code: {}", zcrmLeadId, response.getStatusCode());
            } else {
                logger.warn("Unexpected status updating Lead & Address {} -- {}", zcrmLeadId, response.getStatusCode());
                logger.debug("Update Lead & Address Response body: {}", response.getBody());
            }
        } catch (HttpClientErrorException e) {
            responseBody = e.getResponseBodyAsString();
            logger.error(
                    "HttpClientErrorException - Appointment & Address unable to be added to Lead: {} -- Message: {}",
                    zcrmLeadId, e.getMessage());
        } catch (RestClientException ex) {
            responseBody = "{}";
            logger.error(
                    "RestClientException - Appointment & Address unable to be added to Lead: {} -- Message: {}",
                    zcrmLeadId, ex.getMessage());
        }

        return new ZohoLeadUpsertResponse(responseBody);
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
    public ZohoLeadUpsertResponse createLead(AppointmentRequest appointmentData) {
        String accessToken = tokenService.getAccessToken(ZohoModuleApiName.LEADS);
        JSONObject requestBody = ZohoPayloadUtil.buildLeadCreatePayload(
                appointmentData, zcrmSalesManagerId, ambassadorName
        );

        String responseBody;
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    ZohoUtils.buildEndpoint(zcrmApiBaseUrl, ZohoModuleApiName.LEADS),
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

        return new ZohoLeadUpsertResponse(responseBody);
    }




    /**
     * Retrieves a Lead record from Zoho CRM by its Zoho record ID.
     * <p>
     * This method:
     * <ul>
     *   <li>Obtains an access token for the Leads module.</li>
     *   <li>Builds the GET endpoint URL using the provided Zoho record ID.</li>
     *   <li>Sends the request and checks for a successful (2xx) status;</li>
     *   <li>If the response body is null or blank, logs an info message and returns empty.</li>
     *   <li>Parses the JSON payload, and if the "data" array is missing or empty, logs an info message and returns empty.</li>
     *   <li>Otherwise constructs a {@link ZohoFetchResponse} from the body and returns it wrapped in an {@link Optional}.</li>
     * </ul>
     *
     * @param zcrmLeadId the Zoho CRM record ID of the Lead to fetch
     * @return an Optional containing the parsed {@link ZohoFetchResponse} if found; empty if not found or on error
     */
    public Optional<ZohoFetchResponse> fetchLead(String zcrmLeadId) {
        String token = tokenService.getAccessToken(ZohoModuleApiName.LEADS);
        String url   = ZohoUtils.buildEndpoint(zcrmApiBaseUrl, zcrmLeadId, ZohoModuleApiName.LEADS);

        try {
            ResponseEntity<String> resp = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(ZohoUtils.buildHeaders(token)),
                    String.class
            );

            String body = resp.getBody();

            if (!resp.getStatusCode().is2xxSuccessful() || body == null || body.isBlank()) {
                logger.info("No response body fetching Lead with Zoho record id: {}; status was {}", zcrmLeadId, resp.getStatusCode());
                return Optional.empty();
            }

            JSONObject root = new JSONObject(body);
            JSONArray data = root.optJSONArray("data");
            if (data == null || data.isEmpty()) {
                logger.info("No matching Lead found for zoho record id: {}", zcrmLeadId);
                return Optional.empty();
            }

            return Optional.of(new ZohoFetchResponse(body));

        } catch (RestClientException e) {
            logger.warn("Error fetching Lead with Zoho record id: {}, Message: {}", zcrmLeadId, e.getMessage());
            return Optional.empty();
        }
    }

}
