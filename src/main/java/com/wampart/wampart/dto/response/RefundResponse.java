package com.wampart.wampart.dto.response;

import com.wampart.wampart.enums.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponse {

    private String id;
    private String refundReference;
    private String paymentId;
    private String bookingId;
    private String userId;
    private Double refundAmount;
    private String refundReason;
    private RefundStatus refundStatus;
    private LocalDateTime requestedAt;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String adminNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}