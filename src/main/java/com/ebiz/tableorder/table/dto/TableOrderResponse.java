package com.ebiz.tableorder.table.dto;

import com.ebiz.tableorder.order.dto.OrderDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TableOrderResponse {
    private final int tableNumber;
    private final int totalAmount;
    private final List<OrderDetailDTO> orders;
}
