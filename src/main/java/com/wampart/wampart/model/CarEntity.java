package com.wampart.wampart.model;


import com.wampart.wampart.enums.FuelType;
import com.wampart.wampart.enums.Transmission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cars")
public class CarEntity {
    @Id
    private String id;

    private String brand;
    private String model;
    private Integer yearOfManufacture;
    private String color;
    private String FuelType;
    private FuelType typeOfFuel;
    private Transmission transmission;
    private Integer numberOfPassengers;

    @Indexed(unique = true)
    private String numberPlate;

    private String description;
    private List<String> images = new ArrayList<>();
    private Double pricePerDay;
    private Double currentMileage;
    private Double serviceMileageInterval;
    private Boolean isAvailable;
    private Boolean isInsuranceActive;
    private LocalDateTime insuranceExpiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
