package com.ebiz.tableorder.table.service;

import com.ebiz.tableorder.order.entity.Order;
import com.ebiz.tableorder.order.repository.OrderRepository;
import com.ebiz.tableorder.table.dto.TableSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

    public List<TableSummaryResponse> getAllTablesSummaryToday() {
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to   = from.plusDays(1);

        // 오늘 주문된 모든 Order
        List<Order> allOrders = orderRepo.findByCreatedAtBetween(from, to);

        // 테이블 번호별로 묶기
        Map<Integer, List<Order>> ordersByTable = allOrders.stream()
                .collect(Collectors.groupingBy(o -> o.getTable().getTableNumber()));

        // 각 테이블마다 TableSummaryResponse 생성
        return ordersByTable.entrySet().stream()
                .map(entry -> {
                    int tableNumber = entry.getKey();
                    List<Order> orders = entry.getValue();

                    int totalOrders = orders.size();
                    int totalAmount = orders.stream()
                            .flatMap(o -> o.getItems().stream())
                            .mapToInt(oi -> oi.getMenu().getPrice().intValue() * oi.getQuantity())
                            .sum();

                    // 메뉴별 합산
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
                            .items(new ArrayList<>(grouped.values()))
                            .build();
                })
                .sorted(Comparator.comparingInt(TableSummaryResponse::getTableNumber))
                .toList();
    }
}