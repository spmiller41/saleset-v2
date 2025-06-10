package com.saleset.integration.shorten;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TinyUrlShortenService {

    @Value("${tinyurl.create.token}")
    private String tinyurlCreateToken;

    @Value("${tinyurl.api.endpoint}")
    private String apiUrl;

    @Value("${tinyurl.booking.domain}")
    private String companyBookingDomain;

    private final RestTemplate restTemplate;

    public TinyUrlShortenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String create(String longUrl) {
        // Create the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("url", longUrl);
        requestBody.put("domain", companyBookingDomain);

        // Set the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + tinyurlCreateToken);

        // Create the request entity
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Make the POST request using exchange to capture type-safe response
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        // Extract the tiny URL from the response
        Map<String, Object> response = responseEntity.getBody();
        if (response != null && response.containsKey("data")) {
            Object dataObject = response.get("data");
            if (dataObject instanceof Map) {
                @SuppressWarnings("unchecked") // This cast is safe because we've checked the instance
                Map<String, Object> data = (Map<String, Object>) dataObject;
                return (String) data.get("tiny_url");
            }
        }

        return null; // or throw an appropriate exception
    }

}
