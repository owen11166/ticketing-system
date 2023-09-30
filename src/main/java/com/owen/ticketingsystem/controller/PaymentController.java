package com.owen.ticketingsystem.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owen.ticketingsystem.DTO.OrderDTO;
import com.owen.ticketingsystem.entity.*;
import com.owen.ticketingsystem.repository.OrderItemRepository;
import com.owen.ticketingsystem.repository.OrderRepository;
import com.owen.ticketingsystem.repository.ShippingAddressRepository;
import com.owen.ticketingsystem.repository.UserRepository;
import com.owen.ticketingsystem.service.CartService;
import com.owen.ticketingsystem.service.PayPalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Controller
public class PaymentController {
    @Autowired
    private CartService cartService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PayPalService payPalService;

    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ShippingAddressRepository shippingAddressRepository;

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
    public String fail() {
        return "paymentFailure";
    }


    @GetMapping("/payment-success")
    public ModelAndView handlePayPalSuccess(@RequestParam String paymentId) {

        Optional<Order> orderOptional = orderRepository.findByPaymentId(paymentId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setPay("yes");
            orderRepository.save(order);
        } else {
            System.err.println("No order found for paymentId: " + paymentId);
        }
        ModelAndView modelAndView = new ModelAndView("paymentSuccess");
        modelAndView.addObject("transactionId", paymentId);
        return modelAndView;
    }

    @PostMapping("/payment-success")
    public ResponseEntity<?> handlePayPalSuccess(
        ) {




        return ResponseEntity.ok("Payment details saved successfully");
    }

    @PostMapping("/save-order")
    @Transactional
    public ResponseEntity<?> saveOrder(@RequestBody OrderDTO orderDTO, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        String name = principal.getName();
        User user = userRepository.findByUserName(name);
        Order order = new Order();
        ShippingAddress shippingAddress = new ShippingAddress();


        order.setOrderDate(LocalDate.now());
        order.setPay("no");
        Cart cart = cartService.getCartByUser(user);
        Long total = (long) cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        order.setPrice(total);
        order.setShippingAddress(shippingAddress);
        order.setUser(user);

        shippingAddress.setAddressLine1(orderDTO.getAddressLine1());
        shippingAddress.setPhone(orderDTO.getPhone());
        shippingAddress.setRecipientName(orderDTO.getRecipientName());

        List<CartItem> cartItems = cart.getItems();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProducts(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            order.getOrderItems().add(orderItem);

        }
        orderRepository.save(order);
        shippingAddressRepository.save(shippingAddress);

        String accessToken;
        try {
            accessToken = payPalService.getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get access token from PayPal.", e);
        }
        ResponseEntity<String> payPalResponse = payPalService.createPayment(accessToken, total);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(payPalResponse.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse PayPal response.", e);
        }
        String paymentId = rootNode.path("id").asText();
        order.setPaymentId(paymentId);
        orderRepository.save(order);
        JsonNode linksNode = rootNode.path("links");
        String approvalUrl = null;
        for (JsonNode linkNode : linksNode) {
            if ("approval_url".equals(linkNode.path("rel").asText())) {
                approvalUrl = linkNode.path("href").asText();
                break;
            }
        }
        response.put("approvalUrl", approvalUrl);
        return ResponseEntity.ok(response);

    }
}
