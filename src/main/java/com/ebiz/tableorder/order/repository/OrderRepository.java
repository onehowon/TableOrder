package com.ebiz.tableorder.order.repository;

import com.ebiz.tableorder.order.entity.Order;
import com.ebiz.tableorder.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    /** 오늘 삭제되지 않은 주문만 조회 (cleared=false) */
    List<Order> findByCreatedAtBetweenAndClearedFalse(LocalDateTime from, LocalDateTime to);

    /** 특정 테이블의 오늘 삭제되지 않은 주문만 조회 */
    List<Order> findByTable_TableNumberAndCreatedAtBetweenAndClearedFalse(
            Integer tableNumber,
            LocalDateTime from,
            LocalDateTime to
    );

    List<Order> findByTable_TableNumberAndCreatedAtBetween(
            Integer tableNumber,
            LocalDateTime from,
            LocalDateTime to
    );

    /** 알림용: WAITING & cleared=false */
    List<Order> findByStatusAndClearedFalse(OrderStatus status);

    /** 전체 누적 주문수 (JpaRepository.count() 로도 가능) */
    @Query("SELECT COUNT(o) FROM Order o")
    long countAllOrders();

    /** 전체 누적 유니크 테이블 수 */
    @Query("""
      SELECT COUNT(DISTINCT t.tableNumber)
      FROM Order o
      JOIN o.table t
    """)
    long countDistinctTablesAll();
}