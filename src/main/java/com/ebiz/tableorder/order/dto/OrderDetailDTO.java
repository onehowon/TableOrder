package com.ebiz.tableorder.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderDetailDTO {
    private final Long orderId;
    private final int tableNumber;
    private final String status;
    private final LocalDateTime createdAt;
    private final List<OrderItemDTO> items;
}
