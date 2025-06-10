package com.saleset.integration.shorten;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RebrandlyUrlShortenService {

    @Value("${rebrandly.workspace.id}")
    private String rebrandlyWorkspaceId;

    @Value("${rebrandly.api.key}")
    private String rebrandlyApiKey;

    @Value("${rebrandly.custom.domain}")
    private String rebrandlyCustomDomain;

    private final RestTemplate restTemplate;

    @Autowired
    public RebrandlyUrlShortenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String create(String longUrl) {
        String endpoint = "https://api.rebrandly.com/v1/links";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", rebrandlyApiKey);
        headers.set("workspace", rebrandlyWorkspaceId);

        // Use Map to construct the JSON body
        Map<String, Object> body = new HashMap<>();
        body.put("destination", longUrl);
        Map<String, String> domainMap = new HashMap<>();
        domainMap.put("fullName", rebrandlyCustomDomain);
        body.put("domain", domainMap);

        // Convert Map to JSON String
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Make the POST request
        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, requestEntity, String.class);

        // Extract the shortUrl from the response
        String shortURL = new org.json.JSONObject(response.getBody()).getString("shortUrl");
        return "https://" + shortURL;
    }

}
