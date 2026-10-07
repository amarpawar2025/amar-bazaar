package com.amar.Service;

import com.amar.controller.Wishlist;
import com.amar.modal.Product;
import com.amar.modal.User;

public interface WishlistService {

    Wishlist createwishlist(User user);

    Wishlist getWishlistByUserId(User user);

    Wishlist addProductToWishlist(User user, Product product);
}