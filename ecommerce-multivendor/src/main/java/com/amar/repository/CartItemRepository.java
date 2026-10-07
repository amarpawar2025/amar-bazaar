package com.amar.repository;

import com.amar.modal.Cart;
import com.amar.modal.CartItem;
import com.amar.modal.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {

    CartItem findBycartAndProductAndSize(
            Cart cart,
            Product product,
            String size);
}