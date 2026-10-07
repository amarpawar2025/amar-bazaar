package com.amar.controller;
import com.amar.Service.CartItemService;
import com.amar.Service.CartService;
import com.amar.Service.ProductService;
import com.amar.Service.UserService;
import com.amar.modal.Cart;
import com.amar.modal.CartItem;
import com.amar.modal.Product;
import com.amar.modal.User;
import com.amar.request.AddItemRequest;
import com.amar.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final CartItemService cartItemService;
    private final UserService userService;
    private final ProductService productService;


    // GET USER CART
    @GetMapping
    public ResponseEntity<Cart> findUserCartHandler(
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        User user =
                userService.findUserByJwtToken(jwt);

        Cart cart =
                cartService.findUserCart(user);

        return new ResponseEntity<>(
                cart,
                HttpStatus.OK
        );
    }


    // ADD ITEM TO CART
    @PutMapping("/add")
    public ResponseEntity<CartItem> addCartItemToCart(
            @RequestBody AddItemRequest req,
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        User user =
                userService.findUserByJwtToken(jwt);

        Product product =
                productService.findProductById(
                        req.getProductId()
                );

        CartItem cartItem =
                cartService.addcartItem(
                        user,
                        product,
                        req.getSize(),
                        req.getQuantity()
                );

        return new ResponseEntity<>(
                cartItem,
                HttpStatus.ACCEPTED
        );
    }


    // UPDATE CART ITEM
    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestBody CartItem cartItem,
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        User user =
                userService.findUserByJwtToken(jwt);

        CartItem updatedCartItem = null;

        if (cartItem.getQuantity() > 0) {

            updatedCartItem =
                    cartItemService.updatecartItem(
                            user.getId(),
                            cartItemId,
                            cartItem
                    );
        }

        return new ResponseEntity<>(
                updatedCartItem,
                HttpStatus.ACCEPTED
        );
    }


    // DELETE CART ITEM
    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<ApiResponse> deleteCartItem(
            @PathVariable Long cartItemId,
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        User user =
                userService.findUserByJwtToken(jwt);

        cartItemService.removeCardItem(
                user.getId(),
                cartItemId
        );

        ApiResponse res =
                new ApiResponse();

        res.setMessage(
                "item remove from cart"
        );

        return new ResponseEntity<>(
                res,
                HttpStatus.ACCEPTED
        );
    }
}