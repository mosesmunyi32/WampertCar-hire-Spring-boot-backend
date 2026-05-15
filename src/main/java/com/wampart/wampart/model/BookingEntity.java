package com.wampart.wampart.model;


import com.wampart.wampart.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bookings")
public class BookingEntity {
    @Id
    private String id;

    @Indexed(unique = true)
    private String bookingReference;

    private String userId;
    private String carId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime actualReturnDate;
    private String pickUpLocation;
    private String travelDestination;
    private Integer numberOfDays;
    private Double pricePerDay;
    private Double discount;
    private Double bookingCost;
    private Integer extendedDays;
    private Double extendedDaysCost;
    private BookingStatus bookingStatus;
    private String customerNote;
    private String adminNote;
    private String approvedBy;
    private String mileageStart;
    private String mileageEnd;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

}
