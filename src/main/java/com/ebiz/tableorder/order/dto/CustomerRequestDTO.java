package com.ebiz.tableorder.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequestDTO {
    private Long id;
    private int tableNumber;
    private LocalDateTime createdAt;  // ISO 문자열로 내려받습니다
}