package com.amar.Service;

import com.amar.modal.Cart;
import com.amar.modal.CartItem;
import com.amar.modal.Product;
import com.amar.modal.User;

public interface CartService {

    CartItem addcartItem(
            User user,
            Product product,
            String size,
            int quantity
    );

    Cart findUserCart(User user);

    Cart findByUserId(Long id);

    void clearCart(User user);
}