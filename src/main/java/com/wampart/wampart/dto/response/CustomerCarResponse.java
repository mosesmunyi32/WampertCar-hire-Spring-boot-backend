package com.wampart.wampart.dto.response;


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
public class CustomerCarResponse {
    private String id;
    private String model;
    private Integer yearOfManufacture;
    private String color;
    private String brand;
    private String typeOfFuel;
    private String numberPlate;
    private Boolean isInUse;
    private String transmission;
    private Integer numberOfPassengers;
    private String description;
    private List<String> images;
    private Double pricePerDay;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
