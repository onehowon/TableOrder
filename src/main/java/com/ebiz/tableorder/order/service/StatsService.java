package com.ebiz.tableorder.order.service;

import com.ebiz.tableorder.menu.dto.SalesDataPoint;
import com.ebiz.tableorder.menu.dto.SalesStatsDTO;
import com.ebiz.tableorder.order.repository.OrderItemRepository;
import com.ebiz.tableorder.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
public class StatsService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;

    @Transactional(readOnly = true)
    public SalesStatsDTO getTodaySalesStats() {
        LocalDate today = LocalDate.now();

        // ─── 합계 통계 ────────────────────────────────────
        long totalRevenue = itemRepo.sumRevenueByDate(today);
        long totalOrders  = orderRepo.countByDate(today);
        long totalTables  = orderRepo.countDistinctCustomersByDate(today);

        // ─── 시간대별 매출 DTO 리스트 조회 ───────────────────
        List<SalesDataPoint> rawPoints = itemRepo.sumRevenueByHour(today);

        // ─── Map<시간, 매출> 으로 변환 ─────────────────────
        Map<Integer, Long> revenueMap = rawPoints.stream()
                .collect(Collectors.toMap(
                        SalesDataPoint::getHour,
                        SalesDataPoint::getRevenue
                ));

        // ─── 0시부터 23시까지 빠진 시간은 0으로 채우기 ────────
        List<SalesDataPoint> fullPoints = IntStream.rangeClosed(0, 23)
                .mapToObj(h -> new SalesDataPoint(
                        h,
                        revenueMap.getOrDefault(h, 0L)
                ))
                .collect(Collectors.toList());

        return new SalesStatsDTO(
                totalTables,
                totalOrders,
                totalRevenue,
                fullPoints
        );
    }

    // ... 다른 메서드들
}