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


    private String adminNote;

}
