package com.ebiz.tableorder.table.service;

import com.ebiz.tableorder.order.entity.Order;
import com.ebiz.tableorder.order.repository.OrderRepository;
import com.ebiz.tableorder.table.dto.TableSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TableService {

    private final OrderRepository orderRepo;

    /** 오늘 하루 기준 테이블 요약 (cleared=false) */
    public TableSummaryResponse getSummaryToday(int tableNumber) {
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to   = from.plusDays(1);

        List<Order> orders = orderRepo
                .findByTable_TableNumberAndCreatedAtBetweenAndClearedFalse(tableNumber, from, to);

        int totalOrders = orders.size();
        int totalAmount = orders.stream()
                .flatMap(o -> o.getItems().stream())
                .mapToInt(oi -> oi.getMenu().getPrice().intValue() * oi.getQuantity())
                .sum();

        Map<String, TableSummaryResponse.ItemSummary> grouped = orders.stream()
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.toMap(
                        oi -> oi.getMenu().getName(),
                        oi -> TableSummaryResponse.ItemSummary.builder()
                                .name(oi.getMenu().getName())
                                .quantity(oi.getQuantity())
                                .totalPrice(oi.getMenu().getPrice().intValue() * oi.getQuantity())
                                .build(),
                        TableSummaryResponse.ItemSummary::combine
                ));

        return TableSummaryResponse.builder()
                .tableNumber(tableNumber)
                .totalOrders(totalOrders)
                .totalAmount(totalAmount)
                .items(grouped.values().stream().toList())
                .build();
    }

    /** 전체 테이블 오늘 요약 (cleared=false) */
    public List<TableSummaryResponse> getAllTablesSummaryToday() {
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to   = from.plusDays(1);

        List<Order> allOrders = orderRepo
                .findByCreatedAtBetweenAndClearedFalse(from, to);

        return allOrders.stream()
                .collect(Collectors.groupingBy(o -> o.getTable().getTableNumber()))
                .entrySet().stream()
                .map(e -> {
                    int tbl = e.getKey();
                    List<Order> orders = e.getValue();

                    int totOrders = orders.size();
                    int totAmount = orders.stream()
                            .flatMap(o -> o.getItems().stream())
                            .mapToInt(i -> i.getMenu().getPrice().intValue() * i.getQuantity())
                            .sum();

                    var items = orders.stream()
                            .flatMap(o -> o.getItems().stream())
                            .map(i -> new TableSummaryResponse.ItemSummary(
                                    i.getMenu().getName(),
                                    i.getQuantity(),
                                    i.getMenu().getPrice().intValue() * i.getQuantity()
                            ))
                            .collect(Collectors.toMap(
                                    TableSummaryResponse.ItemSummary::getName,
                                    it -> it,
                                    TableSummaryResponse.ItemSummary::combine
                            ))
                            .values().stream().toList();

                    return TableSummaryResponse.builder()
                            .tableNumber(tbl)
                            .totalOrders(totOrders)
                            .totalAmount(totAmount)
                            .items(items)
                            .build();
                })
                .toList();
    }
}