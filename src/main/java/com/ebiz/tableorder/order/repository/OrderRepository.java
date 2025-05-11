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

    // 특정 테이블의 오늘 주문 목록
    List<Order> findByTable_TableNumberAndCreatedAtBetween(
            Integer tableNumber,
            LocalDateTime start,
            LocalDateTime end
    );

    // 오늘 전체 주문
    List<Order> findByCreatedAtBetween(
            LocalDateTime from,
            LocalDateTime to
    );

    // 특정 상태(예: WAITING) 주문만 조회
    List<Order> findByStatus(OrderStatus status);

    // ── 오늘자 고객(테이블) 수 (중복 제외) ─────────────────────────
    @Query("""
      SELECT COUNT(DISTINCT o.table.tableNumber)
      FROM   Order o
      WHERE  FUNCTION('DATE', o.createdAt) = :today
    """)
    long countDistinctCustomersByDate(@Param("today") LocalDate today);

    // ── 오늘자 주문 건수 ─────────────────────────────────────────
    @Query("""
      SELECT COUNT(o)
      FROM   Order o
      WHERE  FUNCTION('DATE', o.createdAt) = :today
    """)
    long countByDate(@Param("today") LocalDate today);

    // ── 주문 상태 + ETA 업데이트 (setter 없이 JPQL update) ────────────
    @Modifying
    @Query("""
      UPDATE Order o
        SET o.status        = :status,
            o.estimatedTime = :eta
      WHERE o.id = :id
    """)
    int updateStatusAndEta(
            @Param("id")     Long id,
            @Param("status") OrderStatus status,
            @Param("eta")    Integer estimatedTime
    );

    @Modifying
    @Query("delete from Order o where o.table.tableNumber = :tableNumber and o.createdAt between :from and :to")
    void deleteByTable_TableNumberAndCreatedAtBetween(@Param("tableNumber") int tableNumber,
                                                      @Param("from") LocalDateTime from,
                                                      @Param("to")   LocalDateTime to);
}