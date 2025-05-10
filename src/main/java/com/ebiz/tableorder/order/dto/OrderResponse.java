package com.ebiz.tableorder.order.dto;

import com.ebiz.tableorder.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private final Long orderId;
    private final String status;
    private final int tableNumber;
    private final List<OrderItemDTO> items;

    public static OrderResponse from(Order order) {
        List<OrderItemDTO> dtoItems = order.getItems().stream()
                .map(oi -> new OrderItemDTO(
                        oi.getMenu().getName(),
                        oi.getQuantity()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getTable().getTableNumber(),
                dtoItems
        );
    }
}
