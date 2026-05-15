package com.wampart.wampart.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {

    @NotBlank(message = "Payment ID is required")
    private String paymentId;

    @NotBlank(message = "Booking ID is required")
    private String bookingId;

    @NotNull(message = "Refund amount is required")
    private Double refundAmount;

    @NotBlank(message = "Refund reason is required")
    private String refundReason;
}
