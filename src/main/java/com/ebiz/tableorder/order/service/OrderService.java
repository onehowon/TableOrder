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

    public OrderResponse get(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ReportableError(404, "주문을 찾을 수 없습니다."));
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, StatusUpdateRequest req) {
        Order old = orderRepo.findById(orderId)
                .orElseThrow(() -> new ReportableError(404, "주문이 존재하지 않습니다."));

        OrderStatus newStatus = OrderStatus.valueOf(req.getStatus());
        Integer newEta = (newStatus == OrderStatus.DELETED) ? req.getEstimatedTime() : null;

        Order updated = old.toBuilder()
                .status(newStatus)
                .estimatedTime(newEta)
                .deletedAt(
                        newStatus == OrderStatus.DELETED && old.getDeletedAt() == null
                                ? LocalDateTime.now()
                                : newStatus == OrderStatus.WAITING
                                ? null
                                : old.getDeletedAt()
                )
                .cleared(old.isCleared())
                .build();


        Order saved = orderRepo.save(updated);
        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderDetailDTO> getAllToday() {
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to   = from.plusDays(1);
        return orderRepo.findByCreatedAtBetween(from, to)
                .stream()
                .map(this::toDetailDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TableOrderResponse getByTableToday(int tableNumber) {
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to   = from.plusDays(1);
        var orders = orderRepo
                .findByTable_TableNumberAndCreatedAtBetween(tableNumber, from, to);

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
                .deletedAt(o.getDeletedAt())
                .build();
    }
}