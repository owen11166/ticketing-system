package com.owen.ticketingsystem.repository;

import com.owen.ticketingsystem.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem ,Long> {
}
