package com.owen.ticketingsystem.repository;

import com.owen.ticketingsystem.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Products, Long> {
}
