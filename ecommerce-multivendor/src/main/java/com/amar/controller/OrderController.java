package com.amar.controller;

import com.amar.Service.CartService;
import com.amar.Service.OrderService;
import com.amar.Service.PaymentService;
import com.amar.Service.UserService;
import com.amar.domain.PaymentMethod;
import com.amar.modal.Address;
import com.amar.modal.Cart;
import com.amar.modal.Order;
import com.amar.modal.OrderItem;
import com.amar.modal.PaymentOrder;
import com.amar.modal.User;
import com.amar.repository.PaymentOrderRepository;
import com.amar.response.PaymentLinkResponse;
import com.amar.response.RazorpayOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    @org.springframework.beans.factory.annotation.Value("${razorpay.key.id:}")
    private String razorpayKeyId;

    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;
    private final PaymentService paymentService;
    private final PaymentOrderRepository paymentOrderRepository;


    // =========================
    // CREATE ORDER
    // =========================

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestBody Address shippingAddress,
            @RequestParam PaymentMethod paymentMethod,
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        User user =
                userService.findUserByJwtToken(jwt);

        Cart cart =
                cartService.findUserCart(user);

        if (cart == null) {
            throw new Exception("Cart not found");
        }

        Set<Order> orders =
                orderService.createOrder(
                        user,
                        shippingAddress,
                        cart
                );


        // =========================
        // CREATE PAYMENT ORDER
        // =========================

        PaymentOrder paymentOrder =
                paymentService.createOrder(
                        user,
                        orders
                );

        // Save payment order
        paymentOrder =
                paymentOrderRepository.save(
                        paymentOrder
                );

        // Razorpay Standard Checkout: create a server-side Razorpay Order.
        // The amount always comes from the server-side cart, never from the browser.
        if (paymentMethod == PaymentMethod.RAZORPAY) {
            paymentOrder = paymentService.createRazorpayOrder(paymentOrder);

            RazorpayOrderResponse response = new RazorpayOrderResponse(
                    razorpayKeyId,
                    paymentOrder.getRazorpayOrderId(),
                    paymentOrder.getAmount() * 100,
                    "INR",
                    paymentOrder.getId()
            );

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        PaymentLinkResponse response =
                new PaymentLinkResponse();


        // =========================
        // STRIPE
        // =========================

        if (paymentMethod == PaymentMethod.STRIPE) {

            String stripePaymentLink =
                    paymentService.createStripePaymentLink(
                            user,
                            paymentOrder.getAmount(),
                            paymentOrder.getId()
                    );

            response.setPayment_link_url(
                    stripePaymentLink
            );
        }


        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }


    // =========================
    // USER ORDER HISTORY
    // =========================

    @GetMapping("/user")
    public ResponseEntity<List<Order>> usersOrderHistory(
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        User user =
                userService.findUserByJwtToken(jwt);

        List<Order> orders =
                orderService.usersOrderHistory(
                        user.getId()
                );

        return new ResponseEntity<>(
                orders,
                HttpStatus.ACCEPTED
        );
    }


    // =========================
    // FIND ORDER BY ID
    // =========================

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> findOrderById(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId)
            throws Exception {

        userService.findUserByJwtToken(jwt);

        Order order =
                orderService.findOrderById(
                        orderId
                );

        return new ResponseEntity<>(
                order,
                HttpStatus.ACCEPTED
        );
    }


    // =========================
    // GET ORDER ITEM BY ID
    // =========================

    @GetMapping("/item/{orderItemId}")
    public ResponseEntity<OrderItem> getOrderItemById(
            @PathVariable Long orderItemId,
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        userService.findUserByJwtToken(jwt);

        OrderItem orderItem =
                orderService.getOrderItemById(
                        orderItemId
                );

        return new ResponseEntity<>(
                orderItem
                ,        HttpStatus.ACCEPTED
        );
    }


    // =========================
    // CANCEL ORDER
    // =========================

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        User user =
                userService.findUserByJwtToken(jwt);

        Order order =
                orderService.cancelOrder(
                        orderId,
                        user
                );

        return ResponseEntity.ok(order);
    }
}