package com.wampart.wampart.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {
    private String id;
    private String carId;
    private String bookingId;
    private String bookingReference;
    private String tripReference;
    private Double initialFuelLevel;
    private Double initialLevelPrice;
    private String destination;
    private Double amountPaid;
    private Double mileageAtStart;
    private Double mileageAtEnd;

}
