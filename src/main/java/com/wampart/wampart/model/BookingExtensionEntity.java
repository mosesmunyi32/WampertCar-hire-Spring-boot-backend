package com.wampart.wampart.model;

import com.wampart.wampart.enums.ExtensionStatus;
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
@Document(collection = "booking_extension")
public class BookingExtensionEntity {
    @Id
    private String id;

    private String extensionReference;
    private String bookingId;
    private String userId;
    private Integer requestedDays;
    private ExtensionStatus extensionStatus;
    private Double extensionCost;
    private LocalDateTime requestedAt;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String adminNote;
    private String customerNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
