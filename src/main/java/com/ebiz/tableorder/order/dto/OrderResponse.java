package com.ebiz.tableorder.order.dto;

import com.ebiz.tableorder.order.entity.Order;
import com.ebiz.tableorder.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private int tableNumber;
    private String status;
    private Integer estimatedTime;
    private List<ItemDTO> items;
    private LocalDateTime deletedAt;
    private boolean cleared;

    public static OrderResponse from(Order order) {
        Integer eta = order.getEstimatedTime() != null
                ? order.getEstimatedTime()
                : 0;

        int tableNum = order.getTable().getTableNumber();

        List<OrderItem> orderItems = order.getItems();
        if (orderItems == null) {
            orderItems = Collections.emptyList();
        }
        List<ItemDTO> dtos = orderItems.stream()
                .map(ItemDTO::from)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getId())
                .tableNumber(tableNum)
                .status(order.getStatus().name())
                .estimatedTime(eta)
                .items(dtos)
                .deletedAt(order.getDeletedAt())
                .cleared(order.isCleared())
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDTO {
        private String name;
        private int quantity;

        public static ItemDTO from(OrderItem oi) {
            return ItemDTO.builder()
                    .name(oi.getMenu().getName())
                    .quantity(oi.getQuantity())
                    .build();
        }
    }
}