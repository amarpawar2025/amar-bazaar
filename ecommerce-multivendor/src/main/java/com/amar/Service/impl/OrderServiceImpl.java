package com.amar.Service.impl;

import com.amar.Service.OrderService;
import com.amar.modal.*;
import com.amar.repository.AddressRepository;
import com.amar.repository.OrderItemRepository;
import com.amar.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final AddressRepository addressRepository;

    private final OrderItemRepository orderItemRepository;


    // =====================================================
    // CREATE ORDER
    // =====================================================

    @Override
    public Set<Order> createOrder(
            User user,
            Address shippingAddress,
            Cart cart) {

        Address address;

        if (shippingAddress.getId() != null) {

            address =
                    addressRepository
                            .findById(
                                    shippingAddress.getId()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Shipping address not found with id "
                                                    + shippingAddress.getId()
                                    )
                            );

        } else {

            address =
                    addressRepository.save(
                            shippingAddress
                    );
        }


        // -------------------------------------------------
        // GROUP CART ITEMS BY SELLER
        // -------------------------------------------------

        Map<Long, List<CartItem>> itemsBySeller =
                cart.getCarItems()
                        .stream()
                        .collect(
                                Collectors.groupingBy(
                                        item ->
                                                item.getProduct()
                                                        .getSeller()
                                                        .getId()
                                )
                        );


        Set<Order> orders =
                new HashSet<>();


        // -------------------------------------------------
        // CREATE ORDER FOR EACH SELLER
        // -------------------------------------------------

        for (
                Map.Entry<Long, List<CartItem>> entry :
                itemsBySeller.entrySet()
        ) {

            Long sellerId =
                    entry.getKey();

            List<CartItem> items =
                    entry.getValue();


            // -------------------------------------------------
            // TOTAL ORDER PRICE
            // -------------------------------------------------

            int totalOrderPrice =
                    items.stream()
                            .mapToInt(
                                    CartItem::getSellingPrice
                            )
                            .sum();


            // -------------------------------------------------
            // TOTAL ITEMS
            // -------------------------------------------------

            int totalItem =
                    items.stream()
                            .mapToInt(
                                    CartItem::getQuantity
                            )
                            .sum();


            // -------------------------------------------------
            // CREATE ORDER
            // -------------------------------------------------

            Order createOrder =
                    new Order();

            createOrder.setUser(
                    user
            );

            createOrder.setSellerId(
                    sellerId
            );

            createOrder.setTotalMrpPrice(
                    totalOrderPrice
            );

            createOrder.setTotalSellingPrice(
                    totalOrderPrice
            );

            createOrder.setTotalItem(
                    totalItem
            );

            createOrder.setShippingAddress(
                    address
            );


            // -------------------------------------------------
            // ORDER STATUS
            // -------------------------------------------------

            OrderStatus orderStatus =
                    new OrderStatus();

            orderStatus.setStatus(
                    "PENDING"
            );

            createOrder.setOrderStatus(
                    orderStatus
            );


            // -------------------------------------------------
            // PAYMENT STATUS
            // -------------------------------------------------

            createOrder
                    .getPaymentDetails()
                    .setStatus(
                            PaymentStatus.PENDING
                    );


            // -------------------------------------------------
            // SAVE ORDER
            // -------------------------------------------------

            Order saveOrder =
                    orderRepository.save(
                            createOrder
                    );

            orders.add(
                    saveOrder
            );


            // -------------------------------------------------
            // CREATE ORDER ITEMS
            // -------------------------------------------------

            for (
                    CartItem item :
                    items
            ) {

                OrderItem orderItem =
                        new OrderItem();

                orderItem.setOrder(
                        saveOrder
                );

                orderItem.setProduct(
                        item.getProduct()
                );

                orderItem.setMrPrice(
                        item.getProduct()
                );

                orderItem.setQuantity(
                        item.getQuantity()
                );

                orderItem.setSize(
                        item.getSize()
                );

                orderItem.setUserId(
                        item.getUserId()
                );

                orderItem.setSellingPrice(
                        item.getSellingPrice()
                );

                saveOrder
                        .getOrderItems()
                        .add(
                                orderItem
                        );

                orderItemRepository.save(
                        orderItem
                );
            }
        }


        return orders;
    }


    // =====================================================
    // FIND ORDER
    // =====================================================

    @Override
    public Order findOrderById(
            Long id)
            throws Exception {

        return orderRepository
                .findById(id)
                .orElseThrow(() ->
                        new Exception(
                                "order not found with id "
                                        + id
                        )
                );
    }


    // =====================================================
    // USER ORDER HISTORY
    // =====================================================

    @Override
    public List<Order> usersOrderHistory(
            Long userId) {

        // Latest order first
        return orderRepository
                .findByUserIdOrderByOrderDateDesc(
                        userId
                );
    }


    // =====================================================
    // SELLER ORDER
    // =====================================================

    @Override
    public List<Order> OrdersellersOrder(
            Long orderId,
            Long userId) {

        return orderRepository
                .findBySellerId(
                        userId
                );
    }


    // =====================================================
    // UPDATE ORDER STATUS
    // =====================================================

    @Override
    public Order updateOrderStatus(
            Long orderId,
            OrderStatus orderStatus)
            throws Exception {

        Order order =
                findOrderById(
                        orderId
                );

        order.setOrderStatus(
                orderStatus
        );

        return orderRepository.save(
                order
        );
    }


    // =====================================================
    // CANCEL ORDER
    // =====================================================

    @Override
    public Order cancelOrder(
            Long orderId,
            User user)
            throws Exception {

        Order order =
                findOrderById(
                        orderId
                );

        if (!user.getId()
                .equals(
                        order.getUser().getId()
                )) {

            throw new Exception(
                    "you don't have access to this order"
            );
        }


        OrderStatus orderStatus =
                new OrderStatus();

        orderStatus.setStatus(
                "CANCELLED"
        );

        order.setOrderStatus(
                orderStatus
        );


        return orderRepository.save(
                order
        );
    }


    // =====================================================
    // GET ORDER ITEM
    // =====================================================

    @Override
    public OrderItem getOrderItemById(
            Long id)
            throws Exception {

        return orderItemRepository
                .findById(id)
                .orElseThrow(() ->
                        new Exception(
                                "Order item not exists.. "
                        )
                );
    }


    // =====================================================
    // SELLER ORDERS
    // =====================================================

    @Override
    public List<Order> sellersOrder(
            Long id) {

        return List.of();
    }
}