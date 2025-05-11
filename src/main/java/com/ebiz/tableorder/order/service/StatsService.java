package com.ebiz.tableorder.order.service;

import com.ebiz.tableorder.menu.dto.SalesDataPoint;
import com.ebiz.tableorder.menu.dto.SalesStatsDTO;
import com.ebiz.tableorder.order.repository.OrderItemRepository;
import com.ebiz.tableorder.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;

    public SalesStatsDTO getTodayStats() {
        LocalDate today = LocalDate.now();

        long totalCustomers = orderRepo.countDistinctCustomersByDate(today);
        long totalOrders    = orderRepo.countByDate(today);
        long totalRevenue   = itemRepo.sumRevenueByDate(today);

        // 쿼리에서 이미 (int hour, long revenue) 형태로 뽑아줍니다
        List<SalesDataPoint> chartData = itemRepo.sumRevenueByHour(today);

        return new SalesStatsDTO(
                totalCustomers,
                totalOrders,
                totalRevenue,
                chartData
        );
    }
}