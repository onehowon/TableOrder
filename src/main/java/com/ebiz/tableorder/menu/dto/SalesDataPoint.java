package com.ebiz.tableorder.menu.dto;


import lombok.Getter;

@Getter
public class SalesDataPoint {
    private final Integer hour;
    private final Long revenue;

    // JPQL new 구문과 완전히 일치하도록 반드시 선언해야 합니다.
    public SalesDataPoint(Integer hour, Long revenue) {
        this.hour = hour;
        this.revenue = revenue;
    }
}
