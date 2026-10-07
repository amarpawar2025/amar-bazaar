package com.amar.repository;

import com.amar.modal.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository
        extends JpaRepository<Cart, Long> {

    Cart findByUserId(Long id);
}