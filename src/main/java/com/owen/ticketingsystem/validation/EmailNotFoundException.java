package com.owen.ticketingsystem.validation;

public class EmailNotFoundException extends RuntimeException  {
    public EmailNotFoundException(String message) {
        super(message);
    }
}

