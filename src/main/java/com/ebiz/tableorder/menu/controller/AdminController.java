package com.ebiz.tableorder.menu.controller;

import com.ebiz.tableorder.common.CommonResponse;
import com.ebiz.tableorder.menu.dto.MenuDTO;
import com.ebiz.tableorder.menu.dto.MenuRequest;
import com.ebiz.tableorder.menu.dto.MenuUpdateRequest;
import com.ebiz.tableorder.menu.service.MenuService;
import com.ebiz.tableorder.oci.service.OciStorageService;
import com.ebiz.tableorder.order.dto.OrderDetailDTO;
import com.ebiz.tableorder.order.dto.OrderResponse;
import com.ebiz.tableorder.order.dto.StatusUpdateRequest;
import com.ebiz.tableorder.order.service.OrderService;
import com.ebiz.tableorder.table.dto.TableSummaryResponse;
import com.ebiz.tableorder.table.service.TableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MenuService menuService;
    private final OrderService orderService;
    private final TableService tableService;
    private final OciStorageService storageService;

    @PostMapping(value = "/menus", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<MenuDTO>> createMenu(
            @RequestParam String  name,
            @RequestParam String  description,
            @RequestParam Integer price,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        String imageUrl = (file != null && !file.isEmpty()) ? storageService.upload(file) : null;

        MenuRequest req = new MenuRequest(name, description, price);
        MenuDTO dto    = menuService.create(req, imageUrl);
        return ResponseEntity.ok(CommonResponse.success(dto, "메뉴 등록 완료"));
    }


    @PutMapping(value = "/menus/{menuId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<MenuDTO>> updateMenu(
            @PathVariable Long menuId,
            @RequestParam(required = false) String  name,
            @RequestParam(required = false) String  description,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        String imageUrl = (file != null && !file.isEmpty())
                ? storageService.upload(file)
                : null;

        MenuUpdateRequest req = new MenuUpdateRequest(name, description, price, isAvailable);
        MenuDTO dto           = menuService.update(menuId, req, imageUrl);

        return ResponseEntity.ok(CommonResponse.success(dto, "메뉴 수정 완료"));
    }

    @GetMapping("/orders")
    public ResponseEntity<CommonResponse<List<OrderDetailDTO>>> getAllOrders() {
        List<OrderDetailDTO> list = orderService.getAllToday();
        return ResponseEntity.ok(CommonResponse.success(list, "주문 리스트 조회"));
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<CommonResponse<OrderResponse>>
    updateStatus(@PathVariable Long orderId,
                 @RequestBody @Valid StatusUpdateRequest req) {

        OrderResponse resp = orderService.updateStatus(orderId, req.getStatus());
        return ResponseEntity.ok(CommonResponse.success(resp, "상태 변경 완료"));
    }

    @GetMapping("/tables/{tableNumber}/summary")
    public ResponseEntity<CommonResponse<TableSummaryResponse>>
    summary(@PathVariable int tableNumber) {

        TableSummaryResponse resp = tableService.getSummaryToday(tableNumber);
        return ResponseEntity.ok(CommonResponse.success(resp, "요약 조회 완료"));
    }
}
