package com.owen.ticketingsystem.repository;

import com.owen.ticketingsystem.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
}
