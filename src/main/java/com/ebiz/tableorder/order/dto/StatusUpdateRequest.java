package com.ebiz.tableorder.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StatusUpdateRequest {
    @NotBlank
    private String status;
    private Integer estimatedTime;
}