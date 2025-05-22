package com.ebiz.tableorder.menu.dto;

import java.util.List;

public record SalesStatsDTO(
        long totalCustomers,
        long totalOrders,
        long totalRevenue,
        long totalProfit,
        List<SalesDataPoint> salesByHour,

        List<SalesMenuPoint> salesByMenu
) {}