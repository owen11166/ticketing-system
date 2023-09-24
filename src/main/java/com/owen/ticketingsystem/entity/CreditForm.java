package com.owen.ticketingsystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name="credit")
public class CreditForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardName;

    private String cardNumber;

    private String rate;

    private String cvc;
    @Transient
    private boolean saveToDatabase;

    public CreditForm() {
    }

    public CreditForm(String cardName, String cardNumber, String rate, String cvc, boolean saveToDatabase) {
        this.cardName = cardName;
        this.cardNumber = cardNumber;
        this.rate = rate;
        this.cvc = cvc;
        this.saveToDatabase = saveToDatabase;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public boolean isSaveToDatabase() {
        return saveToDatabase;
    }

    public void setSaveToDatabase(boolean saveToDatabase) {
        this.saveToDatabase = saveToDatabase;
    }

    @Override
    public String toString() {
        return "CreditForm{" +
                "id=" + id +
                ", cardName='" + cardName + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", rate='" + rate + '\'' +
                ", cvc='" + cvc + '\'' +
                ", saveToDatabase=" + saveToDatabase +
                '}';
    }
}
