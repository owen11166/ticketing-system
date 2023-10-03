package com.owen.ticketingsystem.repository;

import com.owen.ticketingsystem.entity.Order;
import com.owen.ticketingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {
        List<Order> findByUser(User user);
        Optional<Order> findByPaymentId(String paymentId);
}
