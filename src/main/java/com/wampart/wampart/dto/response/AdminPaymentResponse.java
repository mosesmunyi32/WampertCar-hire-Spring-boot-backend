package com.wampart.wampart.dto.response;


import com.wampart.wampart.enums.ModeOfPayment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPaymentResponse {
    private String id;
    private String bookingId;
    private String amountPaid;
    private String userId;
    private ModeOfPayment modeOfPayment;
    private String mpesaTransactionId;
    private String paymentStatus;
    private String confirmedBy;
    private Boolean hasRefundRequest;
    private String additionalNotes;
    private String createdAt;
    private String updatedAt;


}
