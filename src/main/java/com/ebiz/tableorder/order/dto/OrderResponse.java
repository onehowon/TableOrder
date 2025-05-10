package com.ebiz.tableorder.order.dto;

import com.ebiz.tableorder.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private final Long orderId;
    private final String status;
    private final int tableNumber;
    private final Integer estimatedTime;
    private final List<OrderItemDTO> items;

    public static OrderResponse from(Order o) {
        List<OrderItemDTO> items = Optional.ofNullable(o.getItems()).orElse(List.of()).stream()
                .map(i->new OrderItemDTO(i.getMenu().getName(), i.getQuantity()))
                .toList();
        return new OrderResponse(
                o.getId(), o.getStatus().name(), o.getEstimatedTime(),
                o.getTable().getTableNumber(), items
        );
    }
}
