package com.amar.Service.impl;

import com.amar.Service.CartService;
import com.amar.modal.Cart;
import com.amar.modal.CartItem;
import com.amar.modal.Product;
import com.amar.modal.User;
import com.amar.repository.CartItemRepository;
import com.amar.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public CartItem addcartItem(
            User user,
            Product product,
            String size,
            int quantity) {

        Cart cart = findUserCart(user);

        CartItem existingItem =
                cartItemRepository.findBycartAndProductAndSize(
                        cart,
                        product,
                        size
                );

        if (existingItem != null) {

            int newQuantity =
                    existingItem.getQuantity() + quantity;

            existingItem.setQuantity(newQuantity);

            existingItem.setMrpPrice(
                    newQuantity * product.getMrPrice()
            );

            existingItem.setSellingPrice(
                    newQuantity * product.getSellingPrice()
            );

            return cartItemRepository.save(existingItem);
        }

        CartItem cartItem = new CartItem();

        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setUserId(user.getId());
        cartItem.setSize(size);
        cartItem.setCart(cart);

        int mrpPrice =
                quantity * product.getMrPrice();

        int sellingPrice =
                quantity * product.getSellingPrice();

        cartItem.setMrpPrice(mrpPrice);
        cartItem.setSellingPrice(sellingPrice);

        cart.getCarItems().add(cartItem);

        return cartItemRepository.save(cartItem);
    }

    @Override
    public Cart findUserCart(User user) {

        Cart cart =
                cartRepository.findByUserId(user.getId());

        if (cart == null) {

            cart = new Cart();

            cart.setUser(user);

            cart = cartRepository.save(cart);
        }

        int totalPrice = 0;
        int totalSellingPrice = 0;
        int totalItem = 0;

        for (CartItem cartItem :
                cart.getCarItems()) {

            totalPrice +=
                    cartItem.getMrpPrice();

            totalSellingPrice +=
                    cartItem.getSellingPrice();

            totalItem +=
                    cartItem.getQuantity();
        }

        cart.setTotalMrpPrice(totalPrice);

        cart.setDiscount(
                calculateDiscountPrice(
                        totalPrice,
                        totalSellingPrice
                )
        );

        cart.setTotalSellingPrice(
                totalSellingPrice
        );

        cart.setTotalItem(totalItem);

        return cart;
    }

    @Override
    public Cart findByUserId(Long id) {

        return cartRepository.findByUserId(id);
    }

    /*
     * =====================================================
     * CLEAR CART AFTER SUCCESSFUL PAYMENT
     * =====================================================
     */
    @Override
    @Transactional
    public void clearCart(User user) {

        Cart cart =
                cartRepository.findByUserId(
                        user.getId()
                );

        if (cart == null) {
            return;
        }

        /*
         * Delete all cart items from database.
         */
        if (cart.getCarItems() != null &&
                !cart.getCarItems().isEmpty()) {

            for (CartItem cartItem :
                    cart.getCarItems()) {

                cartItemRepository.delete(
                        cartItem
                );
            }

            cart.getCarItems().clear();
        }

        /*
         * Reset cart totals.
         */
        cart.setTotalMrpPrice(0);

        cart.setTotalSellingPrice(0);

        cart.setTotalItem(0);

        cart.setDiscount(0);

        cartRepository.save(cart);
    }

    private int calculateDiscountPrice(
            int mrpPrice,
            int sellingPrice) {

        return mrpPrice - sellingPrice;
    }
}