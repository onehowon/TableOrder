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

    /** 상태 변경 + ETA 업데이트 (JPQL) */
    @Modifying
    @Query("""
      update Order o
         set o.status        = :status,
             o.estimatedTime = :eta
       where o.id = :orderId
    """)
    int updateStatusAndEta(
            @Param("orderId") Long orderId,
            @Param("status")  OrderStatus status,
            @Param("eta")     Integer eta
    );

    /** 오늘 삭제되지 않은 주문만 조회 (cleared=false) */
    List<Order> findByCreatedAtBetweenAndClearedFalse(LocalDateTime from, LocalDateTime to);

    /** 특정 테이블의 오늘 삭제되지 않은 주문만 조회 */
    List<Order> findByTable_TableNumberAndCreatedAtBetweenAndClearedFalse(
            Integer tableNumber,
            LocalDateTime from,
            LocalDateTime to
    );

    /** 알림용: WAITING & cleared=false */
    List<Order> findByStatusAndClearedFalse(OrderStatus status);

    /** 매출 통계용 (cleared 무시) */
    @Query("""
      SELECT COUNT(DISTINCT o.table.tableNumber)
      FROM Order o
      WHERE FUNCTION('DATE', o.createdAt) = :today
    """)
    long countDistinctCustomersByDate(@Param("today") LocalDate today);


    /**
     * 정산(테이블 초기화) 시에 cleared=true 로 플래그 세팅
     */
    @Modifying
    @Query("""
      update Order o
         set o.cleared = true
       where o.table.tableNumber = :tableNumber
         and o.createdAt between :from and :to
    """)
    void markClearedByTableAndDate(
            @Param("tableNumber") int tableNumber,
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to
    );

    @Query("""
      SELECT COUNT(o)
      FROM Order o
      WHERE FUNCTION('DATE', o.createdAt) = :today
    """)
    long countByDate(@Param("today") LocalDate today);
}