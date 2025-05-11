package com.ebiz.tableorder.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalesDataPoint {
    private String label;    // ex) "18시", "19시"
    private long value;      // 해당 시간대 매출 합
}