package com.ebiz.tableorder.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Builder
@Jacksonized
public class OrderRequest {
    private final int tableNumber;
    private final List<OrderItemReq> items;

    @Getter
    @Builder
    @Jacksonized
    public static class OrderItemReq {
        private final Long menuId;
        private final int quantity;
    }
}
