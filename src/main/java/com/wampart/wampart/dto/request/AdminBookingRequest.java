package com.wampart.wampart.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminBookingRequest {



    @NotBlank(message = "Customer ID is required")
    private String idNumber;

    @NotBlank(message = "Car Number Plate is required is required")
    private String numberPlate;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;


    @NotBlank(message = "Travel destination is required")
    private String travelDestination;

    private String adminNote;


}
