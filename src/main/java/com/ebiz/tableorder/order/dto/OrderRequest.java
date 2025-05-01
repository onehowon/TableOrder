package com.ebiz.tableorder.order.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OrderRequest {
    private final int tableNumber;
    private final List<OrderItemReq> items;

    @Getter @Builder
    public static class OrderItemReq {
        private final Long menuId;
        private final int  quantity;
    }
}