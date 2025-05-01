package com.ebiz.tableorder.table.service;

import com.ebiz.tableorder.order.repository.OrderRepository;
import com.ebiz.tableorder.table.dto.TableSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TableService {

    private final OrderRepository orderRepo;

    /** 오늘 하루 기준 테이블 요약 */
    public TableSummaryResponse getSummaryToday(int tableNumber) {

        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to   = from.plusDays(1);

        /* 1) 오늘의 주문 목록 */
        var orders = orderRepo
                .findByTable_TableNumberAndCreatedAtBetween(tableNumber, from, to);

        int totalOrders = orders.size();

        /* 2) 오늘 총 금액 */
        int totalAmount = orders.stream()
                .flatMap(o -> o.getItems().stream())
                .mapToInt(oi -> oi.getMenu().getPrice().intValue() * oi.getQuantity())
                .sum();

        /* 3) 메뉴별 집계 (mergeFunction으로 합산) */
        Map<String, TableSummaryResponse.ItemSummary> grouped =
                orders.stream()
                        .flatMap(o -> o.getItems().stream())
                        .collect(Collectors.toMap(
                                oi -> oi.getMenu().getName(),
                                oi -> TableSummaryResponse.ItemSummary.builder()
                                        .name(oi.getMenu().getName())
                                        .quantity(oi.getQuantity())
                                        .totalPrice(oi.getMenu().getPrice().intValue() * oi.getQuantity())
                                        .build(),
                                TableSummaryResponse.ItemSummary::combine   // ← 합산
                        ));

        return TableSummaryResponse.builder()
                .tableNumber(tableNumber)
                .totalOrders(totalOrders)
                .totalAmount(totalAmount)
                .items(grouped.values().stream().toList())
                .build();
    }
}