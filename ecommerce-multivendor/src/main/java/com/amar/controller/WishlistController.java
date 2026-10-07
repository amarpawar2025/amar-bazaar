package com.amar.controller;
import com.amar.Service.ProductService;
import com.amar.Service.UserService;
import com.amar.Service.WishlistService;
import com.amar.exceptions.ProductException;
import com.amar.modal.Product;
import com.amar.modal.User;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")

public class WishlistController {
    private  final WishlistService wishlistService;
    private  final UserService userService;
    private  final ProductService productService;

    @GetMapping()
    public ResponseEntity<Wishlist>getwishlistByUserId(
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user= userService.findUserByJwtToken(jwt);
        Wishlist wishlist=wishlistService.getWishlistByUserId(user);
        return ResponseEntity.ok(wishlist);

    }

    @PostMapping("/add-product/{productId}")
    public ResponseEntity<Wishlist> addProductToWishlist(
            @PathVariable Long productId,
            @RequestHeader("Authorization") String jwt) throws Exception {


        Product product = productService.findProductById(productId);
        User user = userService.findUserByJwtToken(jwt);
        Wishlist updatedWishlist = wishlistService.addProductToWishlist(
                user, product);


        return ResponseEntity.ok(updatedWishlist);
    }


}
