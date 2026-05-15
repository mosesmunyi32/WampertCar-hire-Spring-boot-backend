package com.wampart.wampart.dto.response;


import com.wampart.wampart.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBookingResponse {

    private String id;
    private String carId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer numberOfDays;
    private String travelDestination;
    private Double discount;
    private Double pricePerDay;
    private Double bookingCost;
    private BookingStatus bookingStatus;
    private String customerNote;
    private String adminNote;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
