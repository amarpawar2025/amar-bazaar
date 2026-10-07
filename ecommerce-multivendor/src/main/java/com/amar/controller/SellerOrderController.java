package com.amar.controller;

import com.amar.Service.OrderService;
import com.amar.Service.SellerService;
import com.amar.modal.Order;
import com.amar.modal.OrderStatus;
import com.amar.modal.Seller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/orders")
public class SellerOrderController {

    private final OrderService orderService;
    private final SellerService sellerService;


    // GET ALL SELLER ORDERS
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrdersHandler(
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        Seller seller =
                sellerService.getSellerProfile(jwt);

        List<Order> orders =
                orderService.sellersOrder(
                        seller.getId());

        return new ResponseEntity<>(
                orders,
                HttpStatus.ACCEPTED);
    }


    // UPDATE ORDER STATUS
    @PatchMapping(
            "/{orderId}/status/{orderStatus}"
    )
    public ResponseEntity<Order> updateOrderHandler(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId,
            @PathVariable OrderStatus orderStatus)
            throws Exception {

        Order orders =
                orderService.updateOrderStatus(
                        orderId,
                        orderStatus);

        return new ResponseEntity<>(
                orders,
                HttpStatus.ACCEPTED);
    }
}