package com.amar.Service;

import com.amar.modal.Address;
import com.amar.modal.Cart;
import com.amar.modal.Order;
import com.amar.modal.OrderItem;
import com.amar.modal.OrderStatus;
import com.amar.modal.User;

import java.util.List;
import java.util.Set;

public interface OrderService {

    Set<Order> createOrder(
            User user,
            Address shippingAddress,
            Cart cart);

    Order findOrderById(
            Long id) throws Exception;

    List<Order> usersOrderHistory(
            Long userId);

    List<Order> OrdersellersOrder(
            Long orderId,
            Long userId);

    Order updateOrderStatus(
            Long orderId,
            OrderStatus orderStatus)
            throws Exception;

    Order cancelOrder(
            Long orderId,
            User user)
            throws Exception;

    OrderItem getOrderItemById(
            Long id) throws Exception;

    List<Order> sellersOrder(Long id);
}