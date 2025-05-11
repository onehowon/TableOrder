package com.ebiz.tableorder.order.service;

import com.ebiz.tableorder.menu.dto.SalesStatsDTO;
import com.ebiz.tableorder.order.repository.OrderItemRepository;
import com.ebiz.tableorder.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public SalesStatsDTO getTodayStats() {
        LocalDate today = LocalDate.now();

        long totalCustomers = orderRepository.countDistinctCustomersByDate(today);
        long totalOrders    = orderRepository.countByDate(today);
        long totalRevenue   = orderItemRepository.sumRevenueByDate(today);
        var chart           = orderItemRepository.sumRevenueByHour(today);

        return new SalesStatsDTO(
                totalCustomers,
                totalOrders,
                totalRevenue,
                chart
        );
    }
}