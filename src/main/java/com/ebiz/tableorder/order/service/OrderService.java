package com.ebiz.tableorder.order.service;

import com.ebiz.tableorder.common.ReportableError;
import com.ebiz.tableorder.menu.entity.Menu;
import com.ebiz.tableorder.menu.repository.MenuRepository;
import com.ebiz.tableorder.menu.service.MenuService;
import com.ebiz.tableorder.order.dto.OrderDetailDTO;
import com.ebiz.tableorder.order.dto.OrderItemDTO;
import com.ebiz.tableorder.order.dto.OrderRequest;
import com.ebiz.tableorder.order.dto.OrderResponse;
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

    /* ---------------- 주문 생성 ---------------- */
    public OrderResponse create(OrderRequest req) {
        // 1) 테이블 조회
        Table table = tableRepo.findByTableNumber(req.getTableNumber())
                .orElseThrow(() -> new ReportableError(404, "테이블을 찾을 수 없습니다."));

        // 2) 주문 엔티티 생성 및 저장 (PK 확보)
        Order order = Order.builder()
                .table(table)
                .status(OrderStatus.WAITING)
                .build();
        orderRepo.save(order);

        // 3) 주문 아이템 생성 및 저장
        List<OrderItem> savedItems = req.getItems().stream()
                .map(io -> {
                    Menu menu = menuRepo.findById(io.getMenuId())
                            .orElseThrow(() -> new ReportableError(404, "메뉴를 찾을 수 없습니다."));
                    OrderItem oi = OrderItem.builder()
                            .order(order)
                            .menu(menu)
                            .quantity(io.getQuantity())
                            .build();
                    return oi;
                })
                .collect(Collectors.toList());
        itemRepo.saveAll(savedItems);
        List<OrderItemDTO> dtoItems = savedItems.stream()
                .map(oi -> OrderItemDTO.builder()
                        .name(oi.getMenu().getName())
                        .quantity(oi.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getStatus().name(),
                dtoItems
        );
    }

    /* --------------- 주문 상태 조회 --------------- */
    public OrderResponse get(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ReportableError(404, "주문을 찾을 수 없습니다."));
        return OrderResponse.from(order);
    }

    /* --------------- 주문 상태 변경 --------------- */
    public OrderResponse updateStatus(Long orderId, String status) {
        Order origin = orderRepo.findById(orderId)
                .orElseThrow(() -> new ReportableError(404, "주문이 존재하지 않습니다."));

        Order updated = Order.builder()
                .id(origin.getId())
                .table(origin.getTable())
                .status(OrderStatus.valueOf(status))
                .createdAt(origin.getCreatedAt())
                .items(origin.getItems())  // origin.getItems()는 DB에서 로드되므로 null 아님
                .build();
        orderRepo.save(updated);

        return OrderResponse.from(updated);
    }

    /* --------------- 오늘 전체 주문 --------------- */
    @Transactional(readOnly = true)
    public List<OrderDetailDTO> getAllToday() {

        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to   = from.plusDays(1);

        return orderRepo.findByCreatedAtBetween(from, to)
                .stream()
                .map(this::toDetailDto)
                .toList();
    }

    /* --------- 특정 테이블의 오늘 주문 목록 -------- */
    @Transactional(readOnly = true)
    public TableOrderResponse getByTableToday(int tableNumber) {

        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to   = from.plusDays(1);

        List<Order> orders = orderRepo
                .findByTable_TableNumberAndCreatedAtBetween(tableNumber, from, to);

        int totalAmount = orders.stream()
                .flatMap(o -> o.getItems().stream())
                .mapToInt(oi -> oi.getMenu().getPrice().intValue() * oi.getQuantity())
                .sum();

        List<OrderDetailDTO> dtoList = orders.stream()
                .map(this::toDetailDto)
                .toList();

        return TableOrderResponse.builder()
                .tableNumber(tableNumber)
                .totalAmount(totalAmount)
                .orders(dtoList)
                .build();
    }

    /* ---------- 내부 매핑 ---------- */
    private OrderDetailDTO toDetailDto(Order o) {
        List<OrderItemDTO> itemDtos = o.getItems().stream()
                .map(oi -> OrderItemDTO.builder()
                        .name(oi.getMenu().getName())
                        .quantity(oi.getQuantity())
                        .build())
                .toList();

        return OrderDetailDTO.builder()
                .orderId(o.getId())
                .tableNumber(o.getTable().getTableNumber())
                .status(o.getStatus().name())
                .createdAt(o.getCreatedAt())
                .items(itemDtos)
                .build();
    }
}