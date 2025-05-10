package com.ebiz.tableorder.order.dto;

import com.ebiz.tableorder.order.entity.Order;
import com.ebiz.tableorder.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private int tableNumber;
    private String status;
    private int estimatedTime;
    private List<ItemDTO> items;

    public static OrderResponse from(Order order) {
        // 1) estimatedTime이 null일 때 0으로 기본값
        int eta = order.getEstimatedTime() != null
                ? order.getEstimatedTime()
                : 0;

        // 2) 테이블 번호 가져오기
        // Table 엔티티에 맞는 getter를 호출하세요.
        // 예) 테이블 엔티티가 `private int tableNumber;` 필드를 갖고 있다면:
        int tableNum = order.getTable().getTableNumber();
        // 혹은 만약 필드명이 `number`라면:
        // int tableNum = order.getTable().getNumber();

        // 3) 주문 항목 리스트 가져오기
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
