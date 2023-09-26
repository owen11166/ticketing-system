package com.owen.ticketingsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class PayPalService {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    private final String OAUTH_URL = "https://api.sandbox.paypal.com/v1/oauth2/token";
    private final String PAYMENT_URL = "https://api.sandbox.paypal.com/v1/payments/payment";

    private RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(OAUTH_URL, entity, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.getBody());
        String accessToken = rootNode.path("access_token").asText();
        return accessToken;
    }

    public ResponseEntity<String> createPayment(String accessToken,double totalAmount) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        String paymentRequestBody = "{"
                + "\"intent\": \"sale\","
                + "\"payer\": { \"payment_method\": \"paypal\" },"
                + "\"transactions\": [{"
                + "    \"amount\": { \"currency\": \"USD\", \"total\": \"" + totalAmount + "\" },"
                + "    \"description\": \"Sample payment.\""
                + "}],"
                + "\"redirect_urls\": {"
                + "    \"return_url\": \"http://localhost:8080/payment-success\","
                + "    \"cancel_url\": \"http://localhost:8080\""
                + "}"
                + "}";





        HttpEntity<String> entity = new HttpEntity<>(paymentRequestBody, headers);

        return restTemplate.postForEntity(PAYMENT_URL, entity, String.class);
    }

}
