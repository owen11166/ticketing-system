package com.owen.ticketingsystem.repository;

import com.owen.ticketingsystem.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
