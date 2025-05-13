package com.ebiz.tableorder.menu.dto;


import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SalesDataPoint {
    private final Integer hour;
    private final long revenue;

    public SalesDataPoint(Integer hour, long revenue) {
        this.hour    = hour;
        this.revenue = revenue;
    }
}