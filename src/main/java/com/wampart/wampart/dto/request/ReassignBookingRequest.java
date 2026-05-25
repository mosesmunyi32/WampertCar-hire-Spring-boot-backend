package com.wampart.wampart.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReassignBookingRequest {

    private String adminNote;

    @NotNull(message = "Car number plate must be provided")
    private String newCarNumberPlate;



}
