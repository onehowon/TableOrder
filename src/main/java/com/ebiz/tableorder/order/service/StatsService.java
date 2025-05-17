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
        LocalDate tomorrow  = today.plusDays(1);

        long totalRevenue = itemRepo.sumRevenueByDate(today);
        long totalOrders  = orderRepo.countByDate(today);
        long totalTables  = orderRepo.countDistinctCustomersByDate(today);

        // 1) 시간대별 매출
        List<SalesDataPoint> rawHour = itemRepo.sumRevenueByHour(today);
        Map<Integer, Long> hourMap = rawHour.stream()
                .collect(Collectors.toMap(SalesDataPoint::getHour, SalesDataPoint::getRevenue));
        List<SalesDataPoint> fullHour = IntStream.rangeClosed(0, 23)
                .mapToObj(h -> new SalesDataPoint(h, hourMap.getOrDefault(h, 0L)))
                .collect(Collectors.toList());

        List<SalesDataPoint> rawMenu =
                itemRepo.sumProfitByMenu(today, tomorrow);

        // 3) DTO 생성 (5번째 파라미터로 메뉴별 리스트 추가!)
        return new SalesStatsDTO(
                totalTables,
                totalOrders,
                totalRevenue,
                fullHour,
                rawMenu
        );
    }
}