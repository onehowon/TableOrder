package com.ebiz.tableorder.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private final Long orderId;
    private final String status;
}
