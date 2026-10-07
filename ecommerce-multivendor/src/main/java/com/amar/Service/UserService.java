package com.amar.Service;

import com.amar.modal.Address;
import com.amar.modal.Product;
import com.amar.modal.User;

import java.util.List;

public interface UserService {

    // ================= PROFILE =================

    User findUserByJwtToken(
            String jwt
    ) throws Exception;

    User findUserByEmail(
            String email
    ) throws Exception;

    User updateProfile(
            String jwt,
            User user
    ) throws Exception;


    // ================= WATCHLIST =================

    User addToWatchlist(
            String jwt,
            Long productId
    ) throws Exception;

    User removeFromWatchlist(
            String jwt,
            Long productId
    ) throws Exception;

    List<Product> getWatchlist(
            String jwt
    ) throws Exception;


    // ================= ADDRESS =================

    Address addAddress(
            String jwt,
            Address address
    ) throws Exception;

    List<Address> getUserAddresses(
            String jwt
    ) throws Exception;

    void deleteAddress(
            String jwt,
            Long addressId
    ) throws Exception;
}