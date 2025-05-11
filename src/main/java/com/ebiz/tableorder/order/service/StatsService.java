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
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StatsService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;

    @Transactional(readOnly = true)
    public SalesStatsDTO getTodaySalesStats() {
        LocalDate today = LocalDate.now();

        long totalRevenue = itemRepo.sumRevenueByDate(today);
        long totalOrders  = orderRepo.countByDate(today);
        long totalTables  = orderRepo.countDistinctCustomersByDate(today);

        List<SalesDataPoint> salesByHour =
                itemRepo.sumRevenueByHourRaw(today).stream()
                        .map(arr -> {
                            Integer hour   = ((Number) arr[0]).intValue();
                            long    sum    = ((Number) arr[1]).longValue();   // ← long 으로 받아오기
                            return new SalesDataPoint(hour, sum);
                        })
                        .collect(Collectors.toList());


        return new SalesStatsDTO(
                totalTables,
                totalOrders,
                totalRevenue,
                salesByHour
        );
    }

    // ... 나머지 메서드들
}