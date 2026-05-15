package com.wampart.wampart.model;


import com.wampart.wampart.enums.TripStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripEntity {
    @Id
    private String id;

    @Indexed(unique = true)
    private String tripReference;

    private String bookingId;
    private String carId;
    private String userId;
    private String destination;
    private Double initialFuelLevel;
    private Double initialLevelPrice;
    private Double fuelCost;
    private Double mileageAtStart;
    private Double mileageAtEnd;
    private TripStatus tripStatus;
    private String additionalNots;
    private String createdAt;
    private String updatedAt;

}
