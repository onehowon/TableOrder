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

    /* ---------------- 주문 생성 ---------------- */
    public OrderResponse create(OrderRequest req) {
        // 1) 테이블 조회
        Table table = tableRepo.findByTableNumber(req.getTableNumber())
                .orElseThrow(() -> new ReportableError(404, "테이블을 찾을 수 없습니다."));

        // 2) 주문 엔티티 생성 후 저장 (PK 확보)
        Order order = Order.builder()
                .table(table)
                .status(OrderStatus.WAITING)
                .build();
        orderRepo.save(order);

        // 3) 주문 아이템 생성 & 저장
        List<OrderItem> items = req.getItems().stream()
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

        // 4) **다시 로드**: 연관된 items 컬렉션이 채워진 새로운 엔티티를 조회합니다.
        Order fullOrder = orderRepo.findById(order.getId())
                .orElseThrow(() -> new ReportableError(500, "주문 저장 후 조회에 실패했습니다."));

        // 5) DTO 변환
        return OrderResponse.from(fullOrder);
    }

    /* --------------- 주문 단건 조회 --------------- */
    public OrderResponse get(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ReportableError(404, "주문을 찾을 수 없습니다."));
        return OrderResponse.from(order);
    }

    /* --------------- 주문 상태 변경 (+ETA) --------------- */
    public OrderResponse updateStatus(Long orderId, StatusUpdateRequest req) {
        // 1) 기존 주문이 존재하는지 확인
        Order origin = orderRepo.findById(orderId)
                .orElseThrow(() -> new ReportableError(404, "주문이 존재하지 않습니다."));

        // 2) JPQL update 메서드 호출
        OrderStatus newStatus = OrderStatus.valueOf(req.getStatus());
        Integer eta = newStatus == OrderStatus.COOKING ? req.getEstimatedTime() : null;
        int updatedCount = orderRepo.updateStatusAndEta(orderId, newStatus, eta);
        if (updatedCount != 1) {
            throw new ReportableError(500, "주문 상태 업데이트에 실패했습니다.");
        }

        // 3) 변경된 엔티티를 다시 조회해서 DTO 변환
        Order updated = orderRepo.findById(orderId)
                .orElseThrow(() -> new ReportableError(500, "업데이트 후 주문 조회에 실패했습니다."));
        return OrderResponse.from(updated);
    }

    /* --------------- 오늘 전체 주문 조회 --------------- */
    @Transactional(readOnly = true)
    public List<OrderDetailDTO> getAllToday() {
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to   = from.plusDays(1);

        return orderRepo.findByCreatedAtBetween(from, to)
                .stream()
                .map(this::toDetailDto)
                .collect(Collectors.toList());
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
                .collect(Collectors.toList());

        return TableOrderResponse.builder()
                .tableNumber(tableNumber)
                .totalAmount(totalAmount)
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

    public void postRequest(RequestDTO req) {
        // TODO: DB 저장 or 푸시 알림 로직
    }

    public List<OrderAlertDTO> getAlerts() {
        List<Order> newOrders = orderRepo.findByStatus(OrderStatus.WAITING);
        return newOrders.stream()
                .map(o -> {
                    // 1) OrderItem → DTO.Item 변환
                    List<OrderAlertDTO.Item> items = o.getItems().stream()
                            .map(i -> new OrderAlertDTO.Item(
                                    // 실제 엔티티 필드명에 맞춰서 getMenu().getName() 사용
                                    i.getMenu().getName(),
                                    i.getQuantity()
                            ))
                            .collect(Collectors.toList());

                    // 2) OrderAlertDTO 생성
                    return new OrderAlertDTO(
                            // 실제 엔티티 필드명에 맞춰서 getTable().getTableNumber() 사용
                            o.getTable().getTableNumber(),
                            items,
                            o.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());
    }


    private OrderDetailDTO toDetailDto(Order o) {
        List<OrderItemDTO> itemDtos = o.getItems().stream()
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