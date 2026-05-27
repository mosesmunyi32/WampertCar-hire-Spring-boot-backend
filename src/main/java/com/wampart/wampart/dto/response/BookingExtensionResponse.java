package com.wampart.wampart.dto.response;

import com.wampart.wampart.enums.ExtensionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingExtensionResponse {

    private String id;
    private String extensionReference;
    private String bookingId;
    private String userId;
    private Integer requestedDays;
    private Double extensionCost;
    private ExtensionStatus extensionStatus;
    private LocalDateTime requestedAt;
    private String adminNote;
    private String customerNote;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}