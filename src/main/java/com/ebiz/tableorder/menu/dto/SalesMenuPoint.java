package com.ebiz.tableorder.menu.dto;

import lombok.Getter;

@Getter
public class SalesMenuPoint {
    private final Long menuId;
    private final long profit;
    public SalesMenuPoint(Long menuId, long profit) {
        this.menuId = menuId;
        this.profit = profit;
    }
}