package com.ebiz.tableorder.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderAlertDTO {
    private final Integer tableNumber;
    private final List<Item> items;
    private final LocalDateTime createdAt;

    @Getter
    @AllArgsConstructor
    public static class Item {
        private final String menuName;
        private final Integer quantity;
    }
}