package com.ebiz.tableorder.order.service;

import com.ebiz.tableorder.common.ReportableError;
import com.ebiz.tableorder.menu.entity.Menu;
import com.ebiz.tableorder.menu.repository.MenuRepository;
import com.ebiz.tableorder.order.dto.*;
import com.ebiz.tableorder.order.entity.Order;
import com.ebiz.tableorder.order.entity.OrderItem;
import com.ebiz.tableorder.order.entity.OrderStatus;
import com.ebiz.tableorder.order.repository.OrderItemRepository;
import com.ebiz.tableorder.order.repository.OrderRepository;
import com.ebiz.tableorder.table.dto.TableOrderResponse;
import com.ebiz.tableorder.table.entity.Table;
import com.ebiz.tableorder.table.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository     orderRepo;
    private final OrderItemRepository itemRepo;
    private final TableRepository     tableRepo;
    private final MenuRepository      menuRepo;

    /* ---------------- 주문 생성 (변경 없음) ---------------- */
    public OrderResponse create(OrderRequest req) {
        Table table = tableRepo.findByTableNumber(req.getTableNumber())
                .orElseThrow(() -> new ReportableError(404, "테이블을 찾을 수 없습니다."));

        Order order = Order.builder()
                .table(table)
                .status(OrderStatus.WAITING)
                .build();
        orderRepo.save(order);

        var items = req.getItems().stream()
                .map(io -> {
                    Menu menu = menuRepo.findById(io.getMenuId())
                            .orElseThrow(() -> new ReportableError(404, "메뉴를 찾을 수 없습니다."));
                    return OrderItem.builder()
                            .order(order)
                            .menu(menu)
                            .quantity(io.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());
        itemRepo.saveAll(items);

        Order full = orderRepo.findById(order.getId())
                .orElseThrow(() -> new ReportableError(500, "저장 후 조회에 실패했습니다."));
        return OrderResponse.from(full);
    }

    /* --------------- 주문 단건 조회 (변경 없음) --------------- */
    public OrderResponse get(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ReportableError(404, "주문을 찾을 수 없습니다."));
        return OrderResponse.from(order);
    }

    /* --------------- 주문 상태 변경 (+ ETA) --------------- */
    public OrderResponse updateStatus(Long orderId, StatusUpdateRequest req) {
        // 1) 존재 확인
        orderRepo.findById(orderId)
                .orElseThrow(() -> new ReportableError(404, "주문이 존재하지 않습니다."));

        // 2) JPQL 업데이트 호출
        OrderStatus newStatus = OrderStatus.valueOf(req.getStatus());
        Integer eta = (newStatus == OrderStatus.DELETED) ? req.getEstimatedTime() : null;
        int updated = orderRepo.updateStatusAndEta(orderId, newStatus, eta);
        if (updated != 1) {
            throw new ReportableError(500, "주문 상태 업데이트에 실패했습니다.");
        }

        // 3) 변경 후 엔티티 재조회
        Order after = orderRepo.findById(orderId)
                .orElseThrow(() -> new ReportableError(500, "업데이트 후 조회에 실패했습니다."));
        return OrderResponse.from(after);
    }

    /* ------ 오늘 전체 주문 조회 (cleared=false) ------ */
    @Transactional(readOnly = true)
    public List<OrderDetailDTO> getAllToday() {
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to   = from.plusDays(1);
        return orderRepo.findByCreatedAtBetweenAndClearedFalse(from, to)
                .stream()
                .map(this::toDetailDto)
                .collect(Collectors.toList());
    }

    /* -- 특정 테이블 오늘 주문 조회 (cleared=false) -- */
    @Transactional(readOnly = true)
    public TableOrderResponse getByTableToday(int tableNumber) {
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to   = from.plusDays(1);
        var orders = orderRepo
                .findByTable_TableNumberAndCreatedAtBetweenAndClearedFalse(tableNumber, from, to);

        int total = orders.stream()
                .flatMap(o -> o.getItems().stream())
                .mapToInt(i -> i.getMenu().getPrice().intValue() * i.getQuantity())
                .sum();

        var dtoList = orders.stream()
                .map(this::toDetailDto)
                .collect(Collectors.toList());

        return TableOrderResponse.builder()
                .tableNumber(tableNumber)
                .totalAmount(total)
                .orders(dtoList)
                .build();
    }

    /* ------ 오늘 매출 요약 (cleared 무시) ------ */
    @Transactional(readOnly = true)
    public SalesSummaryDTO getTodaySummary() {
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to   = from.plusDays(1);
        var orders = orderRepo.findByCreatedAtBetween(from, to);
        long cnt = orders.size();
        long sum = orders.stream()
                .flatMap(o -> o.getItems().stream())
                .mapToLong(i -> i.getMenu().getPrice().longValue() * i.getQuantity())
                .sum();

        return SalesSummaryDTO.builder()
                .count(cnt)
                .totalAmount(sum)
                .build();
    }

    /* ------ 새 주문 알림 (WAITING & cleared=false) ------ */
    public List<OrderAlertDTO> getAlerts() {
        return orderRepo.findByStatusAndClearedFalse(OrderStatus.WAITING)
                .stream()
                .map(o -> {
                    var items = o.getItems().stream()
                            .map(i -> new OrderAlertDTO.Item(i.getMenu().getName(), i.getQuantity()))
                            .collect(Collectors.toList());
                    return new OrderAlertDTO(o.getTable().getTableNumber(),
                            items,
                            o.getCreatedAt());
                })
                .collect(Collectors.toList());
    }

    /* ----- 엔티티 → DTO 변환 헬퍼 ----- */
    private OrderDetailDTO toDetailDto(Order o) {
        var itemDtos = o.getItems().stream()
                .map(oi -> OrderItemDTO.builder()
                        .name(oi.getMenu().getName())
                        .quantity(oi.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return OrderDetailDTO.builder()
                .orderId(o.getId())
                .tableNumber(o.getTable().getTableNumber())
                .status(o.getStatus().name())
                .createdAt(o.getCreatedAt())
                .items(itemDtos)
                .build();
    }
}