package com.wampart.wampart.model;

import com.wampart.wampart.enums.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refunds")
public class RefundEntity {
    @Id
    private String id;

    @Indexed(unique = true)
    private String refundReference;

    private String paymentId;
    private String bookingId;
    private String userId;
    private Double refundAmount;
    private String refundReason;
    private LocalDateTime refundedAt;
    private RefundStatus refundStatus;
    private String approvedBy;
    private String adminNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
