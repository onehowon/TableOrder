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

    public MenuDTO create(MenuRequest req, String imageUrl) {
        Menu menu = Menu.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .isAvailable(true)
                .imageUrl(imageUrl)
                .build();
        return toDto(menuRepo.save(menu));
    }

    @Transactional
    public MenuDTO update(Long id, MenuUpdateRequest req, String imageUrl) {
        Menu menu = menuRepo.findById(id)
                .orElseThrow(() -> new ReportableError(404, "메뉴를 찾을 수 없음"));

        Menu updated = menu.toBuilder()
                .name(          req.getName()        != null ? req.getName()        : menu.getName())
                .description(   req.getDescription() != null ? req.getDescription() : menu.getDescription())
                .price(         req.getPrice()       != null ? req.getPrice()       : menu.getPrice())
                .isAvailable(   req.getIsAvailable() != null ? req.getIsAvailable() : menu.getIsAvailable())
                .imageUrl(      imageUrl             != null ? imageUrl             : menu.getImageUrl())
                .build();

        return toDto(menuRepo.save(updated));
    }


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

    public void delete(Long id) {
        if (!menuRepo.existsById(id)) {
            throw new ReportableError(404, "삭제할 메뉴를 찾을 수 없습니다.");
        }
        menuRepo.deleteById(id);
    }

    @Transactional
    public void deactivate(Long id) {
        Menu menu = menuRepo.findById(id)
                .orElseThrow(() -> new ReportableError(404, "메뉴를 찾을 수 없습니다."));
        Menu updated = menu.toBuilder()
                .isAvailable(false)
                .build();
        menuRepo.save(updated);
    }

    @Transactional
    public void activate(Long id) {
        Menu menu = menuRepo.findById(id)
                .orElseThrow(() -> new ReportableError(404, "메뉴를 찾을 수 없습니다."));
        Menu updated = menu.toBuilder()
                .isAvailable(true)
                .build();
        menuRepo.save(updated);
    }
}
