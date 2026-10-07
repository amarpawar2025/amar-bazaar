package com.amar.Service.impl;

import com.amar.Service.UserService;
import com.amar.config.JWTProvider;
import com.amar.modal.Address;
import com.amar.modal.Product;
import com.amar.modal.User;
import com.amar.repository.AddressRepository;
import com.amar.repository.ProductRepository;
import com.amar.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final AddressRepository addressRepository;

    private final JWTProvider jwtProvider;


    // =====================================================
    // FIND USER BY JWT
    // =====================================================

    @Override
    public User findUserByJwtToken(
            String jwt
    ) throws Exception {

        String email =
                jwtProvider.getEmailFromJWTToken(jwt);

        return findUserByEmail(email);
    }


    // =====================================================
    // FIND USER BY EMAIL
    // =====================================================

    @Override
    public User findUserByEmail(
            String email
    ) throws Exception {

        User user =
                userRepository.findByEmail(email);

        if (user == null) {

            throw new Exception(
                    "user not found with email-"
                            + email
            );
        }

        return user;
    }


    // =====================================================
    // UPDATE PROFILE
    // =====================================================

    @Override
    public User updateProfile(
            String jwt,
            User updatedUser
    ) throws Exception {

        User existingUser =
                findUserByJwtToken(jwt);


        // ================= FULL NAME =================

        if (updatedUser.getFullName() != null
                && !updatedUser.getFullName()
                .trim()
                .isEmpty()) {

            existingUser.setFullName(
                    updatedUser
                            .getFullName()
                            .trim()
            );
        }


        // ================= MOBILE =================

        if (updatedUser.getMobile() != null
                && !updatedUser.getMobile()
                .trim()
                .isEmpty()) {

            existingUser.setMobile(
                    updatedUser
                            .getMobile()
                            .trim()
            );
        }


        return userRepository.saveAndFlush(
                existingUser
        );
    }


    // =====================================================
    // ADD TO WATCHLIST
    // =====================================================

    @Override
    public User addToWatchlist(
            String jwt,
            Long productId
    ) throws Exception {

        User user =
                findUserByJwtToken(jwt);

        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() ->
                                new Exception(
                                        "Product not found with id-"
                                                + productId
                                )
                        );


        if (user.getWatchlist() == null) {

            user.setWatchlist(
                    new ArrayList<>()
            );
        }


        boolean alreadyExists =
                user.getWatchlist()
                        .stream()
                        .anyMatch(p ->
                                p.getId() != null
                                        && p.getId()
                                        .equals(productId)
                        );


        if (!alreadyExists) {

            user.getWatchlist()
                    .add(product);
        }


        return userRepository
                .saveAndFlush(user);
    }


    // =====================================================
    // REMOVE FROM WATCHLIST
    // =====================================================

    @Override
    public User removeFromWatchlist(
            String jwt,
            Long productId
    ) throws Exception {

        User user =
                findUserByJwtToken(jwt);


        if (user.getWatchlist() != null) {

            user.getWatchlist()
                    .removeIf(product ->
                            product.getId() != null
                                    && product.getId()
                                    .equals(productId)
                    );
        }


        return userRepository
                .saveAndFlush(user);
    }


    // =====================================================
    // GET WATCHLIST
    // =====================================================

    @Override
    public List<Product> getWatchlist(
            String jwt
    ) throws Exception {

        User user =
                findUserByJwtToken(jwt);


        if (user.getWatchlist() == null) {

            return new ArrayList<>();
        }


        return user.getWatchlist();
    }


    // =====================================================
    // ADD ADDRESS
    // =====================================================

    @Override
    public Address addAddress(
            String jwt,
            Address address
    ) throws Exception {

        User user =
                findUserByJwtToken(jwt);


        if (address.getName() == null
                || address.getName()
                .trim()
                .isEmpty()) {

            throw new Exception(
                    "Name is required"
            );
        }


        if (address.getMobile() == null
                || address.getMobile()
                .trim()
                .isEmpty()) {

            throw new Exception(
                    "Mobile is required"
            );
        }


        Address savedAddress =
                addressRepository
                        .save(address);


        user.getAddresses()
                .add(savedAddress);


        userRepository.save(user);


        return savedAddress;
    }


    // =====================================================
    // GET USER ADDRESSES
    // =====================================================

    @Override
    public List<Address> getUserAddresses(
            String jwt
    ) throws Exception {

        User user =
                findUserByJwtToken(jwt);


        return new ArrayList<>(
                user.getAddresses()
        );
    }


    // =====================================================
    // DELETE ADDRESS
    // =====================================================

    @Override
    public void deleteAddress(
            String jwt,
            Long addressId
    ) throws Exception {

        User user =
                findUserByJwtToken(jwt);


        Address address =
                addressRepository
                        .findById(addressId)
                        .orElseThrow(() ->
                                new Exception(
                                        "Address not found"
                                )
                        );


        boolean belongsToUser =
                user.getAddresses()
                        .stream()
                        .anyMatch(a ->
                                a.getId() != null
                                        && a.getId()
                                        .equals(addressId)
                        );


        if (!belongsToUser) {

            throw new Exception(
                    "You don't have access to this address"
            );
        }


        user.getAddresses()
                .removeIf(a ->
                        a.getId() != null
                                && a.getId()
                                .equals(addressId)
                );


        userRepository.save(user);


        addressRepository.delete(address);
    }
}