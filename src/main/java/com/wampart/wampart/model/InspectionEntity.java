package com.wampart.wampart.model;


import com.wampart.wampart.enums.CarCondition;
import com.wampart.wampart.enums.InspectionStatus;
import com.wampart.wampart.enums.InspectionType;
import com.wampart.wampart.enums.RepairStatus;
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
    private String carId;
    private String userId;

    private InspectionType inspectionType;

    private LocalDateTime inspectionDate;

    private InspectionStatus inspectionStatus;

    private CarCondition condition;

    private Boolean isDamaged;



    



    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;




}
