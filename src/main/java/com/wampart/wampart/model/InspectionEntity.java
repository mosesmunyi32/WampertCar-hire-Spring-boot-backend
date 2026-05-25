package com.wampart.wampart.model;


import com.wampart.wampart.enums.CarCondition;
import com.wampart.wampart.enums.CustomerResponse;
import com.wampart.wampart.enums.InspectionStatus;
import com.wampart.wampart.enums.InspectionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inspections")
public class InspectionEntity {

    @Id
    private String id;

    @Indexed(unique = true)
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
    private LocalDateTime inspectionDate;
    private InspectionStatus inspectionStatus;
    private String inspectionComment;
    private List<String> damagedPhotos;
    private CustomerResponse customerResponse;
    private CarCondition condition;
    private String customerComment;
    private Boolean isDamaged;
    private Boolean isDamageChargeRequired;
    private Double damageChargeAmount;







    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;




}
