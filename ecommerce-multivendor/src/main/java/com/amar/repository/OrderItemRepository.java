package com.amar.repository;

import com.amar.modal.Order;
import com.amar.modal.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends  JpaRepository<OrderItem,Long>{
}
