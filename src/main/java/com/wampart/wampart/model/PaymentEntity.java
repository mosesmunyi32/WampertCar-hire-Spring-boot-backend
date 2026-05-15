package com.wampart.wampart.model;


import com.wampart.wampart.enums.ModeOfPayment;
import com.wampart.wampart.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class PaymentEntity {
    @Id
    private String id;

    private String bookingId;
    private String userId;
    private Double amountPaid;
    private ModeOfPayment modeOfPayment;
    private String mpesaTransactionId;
    private PaymentStatus paymentStatus;
    private String confirmedBy;
    private String hasRefundRequest;
    private String additionalNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
