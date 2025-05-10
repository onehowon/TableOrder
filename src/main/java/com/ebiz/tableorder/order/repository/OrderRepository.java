package com.ebiz.tableorder.order.repository;

import com.ebiz.tableorder.order.entity.Order;
import com.ebiz.tableorder.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByTable_TableNumberAndCreatedAtBetween(Integer tableNumber, LocalDateTime start, LocalDateTime end);
    List<Order> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
    List<Order> findByStatus(OrderStatus status);

}
