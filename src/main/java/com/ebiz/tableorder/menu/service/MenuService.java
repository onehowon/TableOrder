package com.ebiz.tableorder.menu.service;

import com.ebiz.tableorder.common.ReportableError;
import com.ebiz.tableorder.menu.dto.MenuUpdateRequest;
import com.ebiz.tableorder.menu.dto.MenuDTO;
import com.ebiz.tableorder.menu.dto.MenuRequest;
import com.ebiz.tableorder.menu.entity.Menu;
import com.ebiz.tableorder.menu.repository.MenuRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {
    private final MenuRepository menuRepo;

    public MenuDTO create(MenuRequest req, String imageUrl){
        Menu e=Menu.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .isAvailable(true)
                .imageUrl(req.getImageUrl())
                .build();
        return toDto(menuRepo.save(e)); }

    public MenuDTO update(Long id, MenuUpdateRequest req, String imageUrl){
        Menu m=menuRepo.findById(id)
                .orElseThrow(()->new ReportableError(404,"메뉴를 찾을 수 없음"));
        Menu updated=Menu.builder()
                .id(m.getId())
                .name(req.getName()!=null?req.getName():m.getName())
                .description(req.getDescription()!=null?req.getDescription():m.getDescription())
                .price(req.getPrice()!=null?req.getPrice():m.getPrice())
                .isAvailable(req.getIsAvailable()!=null?req.getIsAvailable():m.getIsAvailable())
                .imageUrl(req.getImageUrl() != null ? req.getImageUrl() : m.getImageUrl())
                .createdAt(m.getCreatedAt())
                .build();
        return toDto(menuRepo.save(updated)); }

    public List<MenuDTO> getAll(){
        return menuRepo.findAll().stream().map(this::toDto).collect(Collectors.toList()); }

    private MenuDTO toDto(Menu e) {
        return MenuDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .price(e.getPrice())
                .isAvailable(e.getIsAvailable())
                .imageUrl(e.getImageUrl())
                .build();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public MenuDTO getOne(Long id) {
        Menu menu = menuRepo.findById(id)
                .orElseThrow(() -> new ReportableError(404, "메뉴를 찾을 수 없습니다."));
        return toDto(menu);
    }

}
