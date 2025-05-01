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

    public MenuDTO create(MenuRequest req){
        Menu e=Menu.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .isAvailable(true).build();
        return toDto(menuRepo.save(e)); }

    public MenuDTO update(Long id, MenuUpdateRequest req){
        Menu m=menuRepo.findById(id)
                .orElseThrow(()->new ReportableError(404,"메뉴를 찾을 수 없음"));
        Menu updated=Menu.builder()
                .id(m.getId())
                .name(req.getName()!=null?req.getName():m.getName())
                .description(req.getDescription()!=null?req.getDescription():m.getDescription())
                .price(req.getPrice()!=null?req.getPrice():m.getPrice())
                .isAvailable(req.getIsAvailable()!=null?req.getIsAvailable():m.getIsAvailable())
                .createdAt(m.getCreatedAt())
                .build();
        return toDto(menuRepo.save(updated)); }

    public List<MenuDTO> getAll(){
        return menuRepo.findAll().stream().map(this::toDto).collect(Collectors.toList()); }

    private MenuDTO toDto(Menu e){
        return MenuDTO.builder()
                .id(e.getId()).name(e.getName()).description(e.getDescription())
                .price(e.getPrice()).isAvailable(e.getIsAvailable()).build(); }
}
