package com.wampart.wampart.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripRequest {

    @NotBlank(message = "Booking ID is required")
    private String bookingId;

    @NotBlank(message = "Car ID is required")
    private String carId;

    @NotBlank(message = "Starting place is required")
    private String startingPlace;

    @NotBlank(message = "Ending place is required")
    private String endingPlace;

    @NotNull(message = "Initial fuel level is required")
    private Double initialFuelLevel;

    @NotNull(message = "Initial fuel price is required")
    private Double initialFuelPrice;

    @NotNull(message = "Mileage at start is required")
    private Double mileageAtStart;

    private String additionalNotes;
}