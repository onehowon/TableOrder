package com.ebiz.tableorder.order.repository;

import com.ebiz.tableorder.order.entity.Order;
import com.ebiz.tableorder.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByTable_TableNumberAndCreatedAtBetween(Integer tableNumber, LocalDateTime start, LocalDateTime end);
    List<Order> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
    List<Order> findByStatus(OrderStatus status);

    /**
     * 오늘자 중복제거 테이블(고객) 수
     */
    @Query("""
      SELECT COUNT(DISTINCT o.table.tableNumber)
      FROM Order o
      WHERE FUNCTION('DATE', o.createdAt) = :today
    """)
    long countDistinctCustomersByDate(@Param("today") LocalDate today);

    /**
     * 오늘자 주문 건수
     */
    @Query("""
      SELECT COUNT(o)
      FROM Order o
      WHERE FUNCTION('DATE', o.createdAt) = :today
    """)
    long countByDate(@Param("today") LocalDate today);

}
