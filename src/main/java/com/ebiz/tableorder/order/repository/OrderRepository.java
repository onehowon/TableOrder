package com.ebiz.tableorder.order.repository;

import com.ebiz.tableorder.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByTable_TableNumberAndCreatedAtBetween(Integer tableNumber, LocalDateTime start, LocalDateTime end);
}
