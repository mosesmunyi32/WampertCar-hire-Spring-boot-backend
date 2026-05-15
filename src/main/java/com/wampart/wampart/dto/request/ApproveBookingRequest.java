package com.wampart.wampart.dto.request;


import com.wampart.wampart.enums.BookingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ApproveBookingRequest {


    @NotNull(message = "Booking status is required")
    private BookingStatus bookingStatus;

    @NotNull(message = "Please provide an extension note")
    private String adminNote;

}
