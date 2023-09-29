package com.owen.ticketingsystem.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owen.ticketingsystem.entity.Cart;
import com.owen.ticketingsystem.entity.Payment;
import com.owen.ticketingsystem.entity.User;
import com.owen.ticketingsystem.repository.PaymentRepository;
import com.owen.ticketingsystem.repository.UserRepository;
import com.owen.ticketingsystem.service.CartService;
import com.owen.ticketingsystem.service.PayPalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;

@RestController
public class PaymentController {
    @Autowired
    private CartService cartService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PayPalService payPalService;
    @Autowired
    private PaymentRepository paymentRepository;
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
    @GetMapping("/payment-fail")
    public String fail(){
        return  "paymentFailure";
    }


    @GetMapping("/payment-success")
    public ModelAndView handlePayPalSuccess(@RequestParam String paymentId) {

        Payment payment = new Payment();
        payment.setTransactionId(paymentId);
        payment.setAmount(1000.0);  // 請根據實際情況設定
        payment.setPayerEmail("payer@example.com");  // 請根據實際情況設定
        payment.setTimestamp(LocalDateTime.now());

        System.out.println("Attempting to save payment data...");
        paymentRepository.save(payment);
        System.out.println("Payment data saved successfully.");
        ModelAndView modelAndView = new ModelAndView("paymentSuccess");
        modelAndView.addObject("transactionId", paymentId);
        return modelAndView;
    }
    @PostMapping("/payment-success")
    public ResponseEntity<?> handlePayPalSuccess(
            @RequestParam String paymentId,
            @RequestParam String token,
            @RequestParam String PayerID) {

        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setToken(token);
        payment.setPayerId(PayerID);

        // You can set other fields of payment object if necessary.
        // For example: payment.setAmount(...);

        paymentRepository.save(payment);

        return ResponseEntity.ok("Payment details saved successfully");
    }

}
