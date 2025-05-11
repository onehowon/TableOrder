package com.ebiz.tableorder.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SalesStatsDTO {
    private long totalCustomers;         // 총 고객 수
    private long totalOrders;            // 총 주문 건수
    private long totalRevenue;           // 총 매출 금액
    private List<SalesDataPoint> chart;  // 시간대별 매출 차트 데이터
}