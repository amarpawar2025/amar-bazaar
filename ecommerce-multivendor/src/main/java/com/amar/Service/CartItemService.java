package com.amar.Service;


import com.amar.modal.CartItem;

public interface CartItemService {

    CartItem  updatecartItem(Long userid,Long id,CartItem  cartItem) throws Exception;
    void removeCardItem(Long userid,Long cartItemId) throws Exception;
    CartItem findCartItemById(Long id) throws Exception;






}
