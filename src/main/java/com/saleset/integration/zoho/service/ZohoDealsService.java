package com.saleset.integration.zoho.service;

import com.saleset.integration.zoho.constants.ZohoLeadFields;
import com.saleset.integration.zoho.dto.response.ZohoLeadFetchResponse;
import com.saleset.integration.zoho.enums.ZohoModuleApiName;
import com.saleset.integration.zoho.util.ZohoUtils;
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

    public Optional<ZohoLeadFetchResponse> fetchLead(String field, String value) {
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

            return Optional.of(new ZohoLeadFetchResponse(body));
        } catch (RestClientException e) {
            logger.warn("Error fetching Deal from Zoho CRM. Field: {} - Value: {} --- Message: {}",
                    field, value, e.getMessage());
            return Optional.empty();
        }
    }

}
