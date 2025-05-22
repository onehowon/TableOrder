package com.ebiz.tableorder.menu.dto;

import com.ebiz.tableorder.menu.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuUpdateRequest {
    private String name;
    private String description;
    private Integer price;
    private Boolean isAvailable;
    private Category category;
}
