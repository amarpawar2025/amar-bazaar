package com.amar.Service.impl;

import com.amar.Service.CartItemService;
import com.amar.modal.CartItem;
import com.amar.modal.User;
import com.amar.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;

    @Override
    public CartItem updatecartItem(
            Long userid,
            Long id,
            CartItem cartItem) throws Exception {

        CartItem item =
                findCartItemById(id);

        User cartItemUser =
                item.getCart().getUser();

        if (cartItemUser.getId().equals(userid)) {

            item.setQuantity(
                    cartItem.getQuantity());

            item.setMrpPrice(
                    item.getQuantity()
                            * item.getProduct()
                            .getMrPrice());

            item.setSellingPrice(
                    item.getQuantity()
                            * item.getProduct()
                            .getSellingPrice());

            return cartItemRepository.save(item);
        }

        throw new Exception(
                "you can't update this cartItem");
    }

    @Override
    public void removeCardItem(
            Long userid,
            Long cartItemId) throws Exception {

        CartItem item =
                findCartItemById(cartItemId);

        User cartItemUser =
                item.getCart().getUser();

        if (cartItemUser.getId().equals(userid)) {

            cartItemRepository.delete(item);

        } else {

            throw new Exception(
                    "you can't delete this litem");
        }
    }

    @Override
    public CartItem findCartItemById(
            Long id) throws Exception {

        return cartItemRepository
                .findById(id)
                .orElseThrow(() ->
                        new Exception(
                                "cart item not found with id "
                                        + id));
    }
}