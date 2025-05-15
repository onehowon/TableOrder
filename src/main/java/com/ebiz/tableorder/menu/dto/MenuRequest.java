package com.ebiz.tableorder.menu.dto;

import com.ebiz.tableorder.menu.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuRequest {
    private String name;
    private String description;
    private Integer price;
    private Category category;
}
