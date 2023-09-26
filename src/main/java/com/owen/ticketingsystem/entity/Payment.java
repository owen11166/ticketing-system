package com.owen.ticketingsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transactionId;
    private Double amount;
    private String payerEmail;
    private LocalDateTime timestamp;

    private String paymentId;
    private String token;
    private String payerId;

    public Payment(Long id, String transactionId, Double amount, String payerEmail, LocalDateTime timestamp, String paymentId, String token, String payerId) {
        this.id = id;
        this.transactionId = transactionId;
        this.amount = amount;
        this.payerEmail = payerEmail;
        this.timestamp = timestamp;
        this.paymentId = paymentId;
        this.token = token;
        this.payerId = payerId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public Payment() {
    }

    public Payment(Long id, String transactionId, Double amount, String payerEmail, LocalDateTime timestamp) {
        this.id = id;
        this.transactionId = transactionId;
        this.amount = amount;
        this.payerEmail = payerEmail;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    public void setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", payerEmail='" + payerEmail + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
