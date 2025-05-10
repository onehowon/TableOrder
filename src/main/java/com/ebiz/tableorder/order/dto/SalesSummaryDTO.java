package com.ebiz.tableorder.order.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesSummaryDTO {
    private long count;
    private long totalAmount;
}
