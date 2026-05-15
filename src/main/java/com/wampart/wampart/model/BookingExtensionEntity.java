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
@Document(collection = "booking_extensions")
public class BookingExtensionEntity {
    @Id
    private String id;



    @Indexed(unique = true)
    private String extensionReference;

    private String bookingId;
    private String userId;
    private String extensionCost;
    private ExtensionStatus extensionStatus;
    private LocalDateTime requestedAt;
    private String approvedBy;
    private String adminNote;
    private String customerNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
