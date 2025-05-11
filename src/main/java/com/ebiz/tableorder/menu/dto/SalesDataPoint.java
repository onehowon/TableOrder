package com.ebiz.tableorder.menu.dto;


import lombok.Getter;


@Getter
public class SalesDataPoint {
    private final Integer hour;
    private final Double revenue;    // ← BigDecimal → Double 로 변경

    public SalesDataPoint(Integer hour, Double revenue) {
        this.hour    = hour;
        this.revenue = revenue;
    }
}