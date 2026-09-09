package com.wampert.wampert.dto.request;

import com.wampert.wampert.enums.RefundStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundApprovalRequest {
    @NotBlank(message = "Refund ID is required")
    private String refundId;

    @NotNull(message = "Refund status is required")
    private RefundStatus refundStatus;

    private String adminNote;
}
