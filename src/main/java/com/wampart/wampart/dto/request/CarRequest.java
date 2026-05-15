package com.wampart.wampart.dto.request;

import com.wampart.wampart.enums.FuelType;
import com.wampart.wampart.enums.Transmission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarRequest {

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Year of manufacture is required")
    private Integer yearOfManufacture;

    @NotBlank(message = "Color is required")
    private String color;

    @NotNull(message = "Fuel type is required")
    private FuelType typeOfFuel;

    @NotNull(message = "Transmission is required")
    private Transmission transmission;

    @NotNull(message = "Number of passengers is required")
    private Integer numberOfPassengers;

    @NotBlank(message = "Number plate is required")
    private String numberPlate;

    private String description;

    @NotNull(message = "Price per day is required")
    private Double pricePerDay;

    @NotNull(message = "Current mileage is required")
    private Double currentMileage;

    @NotNull(message = "Service mileage interval is required")
    private Double serviceMileageInterval;

    @NotNull(message = "Insurance expiration date is required")
    private LocalDateTime insuranceExpiryDate;


    private List<String> images;

    private Boolean isAvailable;

    private LocalDateTime updatedAt;




}