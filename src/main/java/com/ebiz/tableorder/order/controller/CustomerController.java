package com.ebiz.tableorder.order.controller;

import com.ebiz.tableorder.common.CommonResponse;
import com.ebiz.tableorder.menu.dto.MenuDTO;
import com.ebiz.tableorder.menu.service.MenuService;
import com.ebiz.tableorder.order.dto.OrderRequest;
import com.ebiz.tableorder.order.dto.OrderResponse;
import com.ebiz.tableorder.order.service.OrderService;
import com.ebiz.tableorder.table.dto.TableOrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final MenuService menuService;
    private final OrderService orderService;

    /* ---------- 메뉴 ---------- */

    @GetMapping("/menus")
    public ResponseEntity<CommonResponse<List<MenuDTO>>> menus() {
        List<MenuDTO> list = menuService.getAll();
        return ResponseEntity.ok(CommonResponse.success(list, "메뉴 조회"));
    }

    /* ---------- 주문 ---------- */
    @PostMapping("/orders")
    public ResponseEntity<CommonResponse<OrderResponse>>
    order(@RequestBody @Valid OrderRequest req) {

        OrderResponse resp = orderService.create(req);
        return ResponseEntity.ok(CommonResponse.success(resp, "주문 완료"));
    }

    @GetMapping("/orders/table/{tableNumber}")
    public ResponseEntity<CommonResponse<TableOrderResponse>>
    ordersByTable(@PathVariable int tableNumber) {

        TableOrderResponse resp = orderService.getByTableToday(tableNumber);
        return ResponseEntity.ok(CommonResponse.success(resp, "주문 조회"));
    }
}
