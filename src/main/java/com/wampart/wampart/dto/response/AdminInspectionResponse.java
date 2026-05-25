package com.wampart.wampart.dto.response;



import com.wampart.wampart.enums.CarCondition;
import com.wampart.wampart.enums.CustomerResponse;
import com.wampart.wampart.enums.InspectionStatus;
import com.wampart.wampart.enums.InspectionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminInspectionResponse {
    private String id;
    private String inspectionReference;
    private String bookingId;

    private String carNumberPlate;
    private String carModel;
    private String carBrand;

    private String userFirstName;
    private String userLastName;
    private String userPhoneNumber;
    private String userIdNumber;


    private InspectionType inspectionType;
    private LocalDateTime dateOfInspection;
    private InspectionStatus inspectionStatus;
    private CarCondition condition;
    private String inspectionComment;
    private CustomerResponse customerResponse;
    private String customerComment;
    private Boolean isDamaged;
    private Boolean isDamageChargeRequired;
    private Double damageChargeAmount;
    private List<String> damagedPhotos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


