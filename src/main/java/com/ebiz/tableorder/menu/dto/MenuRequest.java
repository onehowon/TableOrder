package com.ebiz.tableorder.menu.dto;

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
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
}
