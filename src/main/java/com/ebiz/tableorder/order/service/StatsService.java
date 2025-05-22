package com.ebiz.tableorder.order.service;

import com.ebiz.tableorder.menu.dto.SalesDataPoint;
import com.ebiz.tableorder.menu.dto.SalesMenuPoint;
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
        LocalDateTime startOfToday    = today.atStartOfDay();
        LocalDateTime startOfTomorrow = today.plusDays(1).atStartOfDay();

        long totalRevenue   = itemRepo.sumRevenueByDate(today);
        long totalOrders    = orderRepo.countByDate(today);
        long totalCustomers = orderRepo.countDistinctCustomersByDate(today);

        // 시간대별 매출
        List<SalesDataPoint> rawHour = itemRepo.sumRevenueByHour(today);
        Map<Integer, Long> hourMap = rawHour.stream()
                .collect(Collectors.toMap(SalesDataPoint::getHour, SalesDataPoint::getRevenue));
        List<SalesDataPoint> salesByHour = IntStream.rangeClosed(0, 23)
                .mapToObj(h -> new SalesDataPoint(h, hourMap.getOrDefault(h, 0L)))
                .collect(Collectors.toList());

        // 메뉴별 이윤
        List<SalesMenuPoint> salesByMenu = itemRepo.sumProfitByMenu(startOfToday, startOfTomorrow);
        long totalProfit = salesByMenu.stream()
                .mapToLong(SalesMenuPoint::getProfit)
                .sum();

        return new SalesStatsDTO(
                totalCustomers,
                totalOrders,
                totalRevenue,
                totalProfit,
                salesByHour,
                salesByMenu
        );
    }
}