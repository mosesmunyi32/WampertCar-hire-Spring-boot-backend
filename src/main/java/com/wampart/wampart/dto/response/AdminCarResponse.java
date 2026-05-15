package com.wampart.wampart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCarResponse {

        private String id;
        private String model;
        private Integer yearOfManufacture;
        private String color;
        private String brand;
        private String typeOfFuel;
        private String numberPlate;
        private String transmission;
        private Integer numberOfPassengers;
        private String description;
        private List<String> images = new ArrayList<>();
        private Double pricePerDay;
        private Boolean isAvailable;
        private Double currentMileage;
        private Double serviceMileageInterval;
        private LocalDateTime insuranceExpiryDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;


}
