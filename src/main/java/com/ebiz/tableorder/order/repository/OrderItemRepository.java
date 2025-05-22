package com.ebiz.tableorder.order.repository;

import com.ebiz.tableorder.menu.dto.SalesDataPoint;
import com.ebiz.tableorder.menu.dto.SalesMenuPoint;
import com.ebiz.tableorder.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /** 오늘자 총 매출 */
    @Query("""
      SELECT COALESCE(SUM(i.quantity * i.menu.price), 0)
      FROM OrderItem i
      WHERE FUNCTION('DATE', i.order.createdAt) = :today
    """)
    long sumRevenueByDate(@Param("today") LocalDate today);

    /** 오늘자 시간대별 매출 DTO 반환 */
    @Query("""
      SELECT new com.ebiz.tableorder.menu.dto.SalesDataPoint(
        HOUR(i.order.createdAt),
        COALESCE(SUM(i.quantity * i.menu.price), 0)
      )
      FROM OrderItem i
      WHERE FUNCTION('DATE', i.order.createdAt) = :today
      GROUP BY HOUR(i.order.createdAt)
      ORDER BY HOUR(i.order.createdAt)
    """)
    List<SalesDataPoint> sumRevenueByHour(@Param("today") LocalDate today);

    /** 오늘자 메뉴별 이윤 집계 (price - cost) * quantity */
    @Query("""
      SELECT new com.ebiz.tableorder.menu.dto.SalesMenuPoint(
        i.menu.name,
        COALESCE(SUM((i.menu.price - i.menu.cost) * i.quantity), 0)
      )
      FROM OrderItem i
      WHERE FUNCTION('DATE', i.order.createdAt) = :today
      GROUP BY i.menu.name
      ORDER BY i.menu.name
    """)
    List<SalesMenuPoint> sumProfitByMenu(@Param("today") LocalDate today);

    /** 특정 기간({startDateTime} ~ {endDateTime}) 메뉴별 이윤 집계 */
    @Query("""
      SELECT new com.ebiz.tableorder.menu.dto.SalesMenuPoint(
        i.menu.name,
        COALESCE(SUM((i.menu.price - i.menu.cost) * i.quantity), 0)
      )
      FROM OrderItem i
      WHERE i.order.createdAt >= :startDateTime
        AND i.order.createdAt <  :endDateTime
      GROUP BY i.menu.name
      ORDER BY i.menu.name
    """)
    List<SalesMenuPoint> sumProfitByMenu(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime")   LocalDateTime endDateTime
    );
}