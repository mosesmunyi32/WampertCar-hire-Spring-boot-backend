package com.wampert.wampert.dto.response;


import com.wampert.wampert.enums.CarCondition;
import com.wampert.wampert.enums.CustomerResponseStatus;
import com.wampert.wampert.enums.InspectionStatus;
import com.wampert.wampert.enums.InspectionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInspectionResponse {

    private String id;
    private String inspectionReference;
    private String bookingId;


    private String carNumberPlate;
    private String carModel;
    private String carBrand;


    private InspectionType inspectionType;
    private LocalDateTime dateOfInspection;
    private InspectionStatus inspectionStatus;
    private CarCondition condition;
    private CustomerResponseStatus customerResponse;
    private String customerComment;
    private Boolean isDamaged;
    private Boolean isDamageChargeRequired;
    private Double damageChargeAmount;
    private List<String> damagedPhotos;
    private String inspectionComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
