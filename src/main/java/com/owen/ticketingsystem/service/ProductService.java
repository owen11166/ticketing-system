package com.owen.ticketingsystem.service;

import com.owen.ticketingsystem.entity.Products;

import java.util.List;


public interface ProductService {
    List<Products> getAllProducts();

    void save(Products product);

    Products findProductById(Long id);
}
