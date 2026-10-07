package com.amar.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.amar.Service.UserService;
import com.amar.modal.Address;
import com.amar.modal.Product;
import com.amar.modal.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // =====================================================
    // PROFILE - GET
    // =====================================================

    @GetMapping("/users/profile")
    public ResponseEntity<User> createUserHandler(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user =
                userService.findUserByJwtToken(jwt);

        return ResponseEntity.ok(user);
    }

    // =====================================================
    // PROFILE - UPDATE
    // =====================================================

    @PutMapping("/users/profile")
    public ResponseEntity<User> updateProfile(
            @RequestHeader("Authorization") String jwt,
            @RequestBody User user
    ) throws Exception {

        User updatedUser =
                userService.updateProfile(
                        jwt,
                        user
                );

        return ResponseEntity.ok(updatedUser);
    }

    // =====================================================
    // ADD WATCHLIST
    // =====================================================

    @PostMapping("/users/watchlist/{productId}")
    public ResponseEntity<User> addToWatchlist(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long productId
    ) throws Exception {

        User user =
                userService.addToWatchlist(
                        jwt,
                        productId
                );

        return ResponseEntity.ok(user);
    }

    // =====================================================
    // REMOVE WATCHLIST
    // =====================================================

    @DeleteMapping("/users/watchlist/{productId}")
    public ResponseEntity<User> removeFromWatchlist(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long productId
    ) throws Exception {

        User user =
                userService.removeFromWatchlist(
                        jwt,
                        productId
                );

        return ResponseEntity.ok(user);
    }

    // =====================================================
    // GET WATCHLIST
    // =====================================================

    @GetMapping("/users/watchlist")
    public ResponseEntity<List<Product>> getWatchlist(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        List<Product> watchlist =
                userService.getWatchlist(jwt);

        return ResponseEntity.ok(watchlist);
    }

    // =====================================================
    // ADD ADDRESS
    // =====================================================

    @PostMapping("/users/addresses")
    public ResponseEntity<Address> addAddress(
            @RequestHeader("Authorization") String jwt,
            @RequestBody Address address
    ) throws Exception {

        Address savedAddress =
                userService.addAddress(
                        jwt,
                        address
                );

        return ResponseEntity.ok(
                savedAddress
        );
    }

    // =====================================================
    // GET ADDRESSES
    // =====================================================

    @GetMapping("/users/addresses")
    public ResponseEntity<List<Address>> getUserAddresses(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        List<Address> addresses =
                userService.getUserAddresses(jwt);

        return ResponseEntity.ok(
                addresses
        );
    }

    // =====================================================
    // DELETE ADDRESS
    // =====================================================

    @DeleteMapping("/users/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long addressId
    ) throws Exception {

        userService.deleteAddress(
                jwt,
                addressId
        );

        return ResponseEntity.ok(
                "Address deleted successfully"
        );
    }
}