package com.owen.ticketingsystem.repository;

import com.owen.ticketingsystem.entity.Cart;
import com.owen.ticketingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUser(User user);
}
