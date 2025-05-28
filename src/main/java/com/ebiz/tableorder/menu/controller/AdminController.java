package com.ebiz.tableorder.menu.controller;

import com.ebiz.tableorder.common.CommonResponse;
import com.ebiz.tableorder.menu.dto.MenuDTO;
import com.ebiz.tableorder.menu.dto.MenuRequest;
import com.ebiz.tableorder.menu.dto.MenuUpdateRequest;
import com.ebiz.tableorder.menu.dto.SalesStatsDTO;
import com.ebiz.tableorder.menu.entity.Category;
import com.ebiz.tableorder.menu.service.MenuService;
import com.ebiz.tableorder.oci.service.OciStorageService;
import com.ebiz.tableorder.order.dto.*;
import com.ebiz.tableorder.order.service.OrderService;
import com.ebiz.tableorder.order.service.RequestService;
import com.ebiz.tableorder.order.service.StatsService;
import com.ebiz.tableorder.table.dto.TableSummaryResponse;
import com.ebiz.tableorder.table.service.TableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MenuService menuService;
    private final OrderService orderService;
    private final TableService tableService;
    private final OciStorageService storageService;
    private final StatsService statsService;
    private final RequestService requestService;

    @PostMapping(value = "/menus", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<MenuDTO>> createMenu(
            @RequestParam String  name,
            @RequestParam String  description,
            @RequestParam Integer price,
            @RequestParam Category category,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        String imageUrl = (file != null && !file.isEmpty()) ? storageService.upload(file) : null;

        MenuRequest req = new MenuRequest(name, description, price, category);
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
            @RequestParam(required = false) Category category,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        String imageUrl = (file != null && !file.isEmpty())
                ? storageService.upload(file)
                : null;

        MenuUpdateRequest req = new MenuUpdateRequest(name, description, price, isAvailable, category);
        MenuDTO dto           = menuService.update(menuId, req, imageUrl);

        return ResponseEntity.ok(CommonResponse.success(dto, "메뉴 수정 완료"));
    }

    @GetMapping("/orders")
    public CommonResponse<List<OrderDetailDTO>> listOrders(){
        return CommonResponse.success(orderService.getAllToday(), "주문 리스트 조회 완료");
    }

    /** 주문 상태 변경 (+ETA) */
    @PutMapping("/orders/{orderId}/status")
    public CommonResponse<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @RequestBody @Valid StatusUpdateRequest req
    ) {
        OrderResponse resp = orderService.updateStatus(orderId, req);
        return CommonResponse.success(resp, "상태 변경 완료");
    }

    @GetMapping("/orders/today-summary")
    public CommonResponse<SalesSummaryDTO> salesSummary(){
        return CommonResponse.success(orderService.getTodaySummary(), "매출 요약");
    }

    @GetMapping("/tables/{tableNumber}/summary")
    public ResponseEntity<CommonResponse<TableSummaryResponse>>
    summary(@PathVariable int tableNumber) {

        TableSummaryResponse resp = tableService.getSummaryToday(tableNumber);
        return ResponseEntity.ok(CommonResponse.success(resp, "요약 조회 완료"));
    }

    @GetMapping("/menus")
    public ResponseEntity<CommonResponse<List<MenuDTO>>> listMenus() {
        List<MenuDTO> list = menuService.getAll();
        return ResponseEntity.ok(CommonResponse.success(list, "메뉴 목록 조회"));
    }

    @DeleteMapping("/menus/{menuId}")
    public ResponseEntity<CommonResponse<Void>> deleteMenu(@PathVariable Long menuId) {
        menuService.delete(menuId);
        return ResponseEntity.ok(CommonResponse.success(null, "메뉴 삭제 완료"));
    }

    @GetMapping("/tables/summary")
    public CommonResponse<TableSummaryResponse> tableSummary(@RequestParam int table){
        return CommonResponse.success(tableService.getSummaryToday(table),"테이블 요약");
    }

    @GetMapping("/tables/summary-all")
    public CommonResponse<List<TableSummaryResponse>> tableSummaryAll(){
        return CommonResponse.success(tableService.getAllTablesSummaryToday(),"전체 테이블 요약");
    }

    @GetMapping("/alerts")
    public CommonResponse<List<OrderAlertDTO>> getAlerts() {
        List<OrderAlertDTO> orderAlerts = orderService.getAlerts();
        orderAlerts.sort(Comparator.comparing(OrderAlertDTO::getCreatedAt).reversed());
        return CommonResponse.success(orderAlerts, "주문 알림 조회 완료");
    }

    @GetMapping("/requests")
    public CommonResponse<List<CustomerRequestDTO>> listRequests() {
        List<CustomerRequestDTO> dtos = requestService.getTodayRequests().stream()
                .map(r -> new CustomerRequestDTO(r.getId(), r.getTableNumber(), r.getCreatedAt()))
                .collect(Collectors.toList());
        return CommonResponse.success(dtos, "직원 호출 목록 조회 완료");
    }


    @GetMapping("/sales")
    public CommonResponse<SalesStatsDTO> getSalesStats() {
        SalesStatsDTO dto = statsService.getCumulativeSalesStats();
        return CommonResponse.success(dto, "누적 매출 통계 조회 완료");
    }

    @DeleteMapping("/requests/{id}")
    public CommonResponse<Void> deleteRequest(@PathVariable Long id) {
        requestService.deleteById(id);
        return CommonResponse.success(null, "직원 호출 처리 완료");
    }

}
