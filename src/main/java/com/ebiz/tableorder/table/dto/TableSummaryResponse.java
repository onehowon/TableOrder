package com.ebiz.tableorder.table.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TableSummaryResponse {
    private final int tableNumber;
    private final int totalOrders;
    private final int totalAmount;
    private final List<ItemSummary> items;
    @Getter
    @Builder
    @AllArgsConstructor
    public static class ItemSummary {
        private final String name;
        private final int quantity;
        private final int totalPrice;
        public static ItemSummary combine(ItemSummary a, ItemSummary b){
            return new ItemSummary(a.name, a.quantity+b.quantity, a.totalPrice+b.totalPrice);
        }
    }
}