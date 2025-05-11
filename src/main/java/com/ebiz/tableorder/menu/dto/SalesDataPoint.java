package com.ebiz.tableorder.menu.dto;


import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SalesDataPoint {
    private final Integer hour;
    private final BigDecimal revenue;   // ← BigDecimal 으로 맞춰줍니다

    public SalesDataPoint(Integer hour, BigDecimal revenue) {
        this.hour    = hour;
        this.revenue = revenue;
    }
}