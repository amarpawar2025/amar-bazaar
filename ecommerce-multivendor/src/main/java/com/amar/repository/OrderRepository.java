package com.amar.repository;

import com.amar.modal.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    // User order history
    List<Order> findByUserId(Long userId);

    // Latest orders first
    List<Order> findByUserIdOrderByOrderDateDesc(
            Long userId
    );

    // Seller orders
    List<Order> findBySellerId(Long sellerId);
}