package com.amar.Service.impl;

import com.amar.Service.WishlistService;
import com.amar.controller.Wishlist;
import com.amar.modal.Product;
import com.amar.modal.User;
import com.amar.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;

    @Override
    public Wishlist createwishlist(User user) {
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        return wishlistRepository.save(wishlist);
    }

    @Override
    public Wishlist getWishlistByUserId(User user) {

       Wishlist wishlist=wishlistRepository.findByUserId(user.getId());
       if (wishlist==null){
           wishlist=createwishlist(user);
       }
       return wishlist;
    }

    @Override
    public Wishlist addProductToWishlist(User user, Product product) {
        Wishlist wishlist=getWishlistByUserId(user);

        if(wishlist.getProducts().contains(product)){
            wishlist.getProducts().remove(product);

        }
        else wishlist.getProducts().add(product);

        return wishlistRepository.save(wishlist);
    }
}