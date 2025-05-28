package com.ebiz.tableorder.order.service;

import com.ebiz.tableorder.menu.dto.SalesDataPoint;
import com.ebiz.tableorder.menu.dto.SalesMenuPoint;
import com.ebiz.tableorder.menu.dto.SalesStatsDTO;
import com.ebiz.tableorder.order.repository.OrderItemRepository;
import com.ebiz.tableorder.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public SalesStatsDTO getCumulativeSalesStats() {
        long totalOrders    = orderRepo.countAllOrders();
        long totalCustomers = orderRepo.countDistinctTablesAll();
        long totalRevenue   = itemRepo.sumRevenueAll();

        List<SalesDataPoint> rawHour = itemRepo.sumRevenueByHourAll();
        Map<Integer, Long> hourMap = rawHour.stream()
                .collect(Collectors.toMap(SalesDataPoint::getHour, SalesDataPoint::getRevenue));
        List<SalesDataPoint> salesByHour = IntStream.rangeClosed(0, 23)
                .mapToObj(h -> new SalesDataPoint(h, hourMap.getOrDefault(h, 0L)))
                .collect(Collectors.toList());

        List<SalesMenuPoint> salesByMenu = itemRepo.sumProfitByMenuAll();
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