package com.ebiz.tableorder.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdateRequest {
    @NotBlank
    private String status;
    private Integer estimatedTime;
}