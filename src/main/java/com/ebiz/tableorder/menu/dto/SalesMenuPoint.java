package com.ebiz.tableorder.menu.dto;

import lombok.Getter;

@Getter
public class SalesMenuPoint {
    private final String menuName;
    private final long profit;

    public SalesMenuPoint(String menuName, long profit) {
        this.menuName = menuName;
        this.profit   = profit;
    }
}