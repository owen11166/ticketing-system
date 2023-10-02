package com.owen.ticketingsystem.repository;

import com.owen.ticketingsystem.entity.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress,Long> {
}
