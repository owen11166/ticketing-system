package com.owen.ticketingsystem.repository;

import com.owen.ticketingsystem.entity.CartItem;
import com.owen.ticketingsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem ,Long> {


}
