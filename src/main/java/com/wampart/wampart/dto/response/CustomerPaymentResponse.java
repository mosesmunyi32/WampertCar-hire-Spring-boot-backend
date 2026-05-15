package com.wampart.wampart.dto.response;

import com.wampart.wampart.enums.ModeOfPayment;
import com.wampart.wampart.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPaymentResponse {
    private String id;
    private String bookingId;
    private String amountPaid;
    private ModeOfPayment modeOfPayment;
    private PaymentStatus paymentStatus;
    private String hasRefundRequest;
    private String additionalNoes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
