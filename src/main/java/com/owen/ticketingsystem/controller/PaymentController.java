package com.owen.ticketingsystem.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owen.ticketingsystem.entity.Cart;
import com.owen.ticketingsystem.entity.User;
import com.owen.ticketingsystem.repository.UserRepository;
import com.owen.ticketingsystem.service.CartService;
import com.owen.ticketingsystem.service.PayPalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;

@RestController
public class PaymentController {
    @Autowired
    private CartService cartService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PayPalService payPalService;

    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestBody Cart cart, Principal principal) {
        // Calculate the total amount based on the cart details

        String username = principal.getName();

        User user = userRepository.findByUserName(username);

        cart = cartService.getCartByUser(user);
        double totalAmount = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        // Get the access token from PayPal
        String accessToken;
        try {
            accessToken = payPalService.getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get access token from PayPal.", e);
        }

        // Use the `createPayment` method to create a payment in PayPal with the access token
        ResponseEntity<String> response = payPalService.createPayment(accessToken, totalAmount);

        // Parse the PayPal response to get the approval URL
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse PayPal response.", e);
        }

        JsonNode linksNode = rootNode.path("links");
        String approvalUrl = null;
        for (JsonNode linkNode : linksNode) {
            if ("approval_url".equals(linkNode.path("rel").asText())) {
                approvalUrl = linkNode.path("href").asText();
                break;
            }
        }

        if (approvalUrl == null) {
            throw new RuntimeException("Failed to extract approval_url from PayPal response.");
        }

        return ResponseEntity.ok().body(Collections.singletonMap("redirectUrl", approvalUrl));
    }
}
