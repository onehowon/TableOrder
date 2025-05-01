package com.ebiz.tableorder.order.repository;

import com.ebiz.tableorder.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
