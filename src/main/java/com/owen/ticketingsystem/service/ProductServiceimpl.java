package com.owen.ticketingsystem.service;

import com.owen.ticketingsystem.entity.Products;
import com.owen.ticketingsystem.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Transactional
public class ProductServiceimpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;


    @Override
    public List<Products> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public void save(Products product) {
         productRepository.save(product);
    }

    @Override
    public Products findProductById(Long id) {
         return productRepository.findById(id).orElse(null);
    }
}
