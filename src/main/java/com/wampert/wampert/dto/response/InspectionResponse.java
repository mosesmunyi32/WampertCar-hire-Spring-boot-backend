package com.wampert.wampert.dto.response;

import com.wampert.wampert.enums.CarCondition;
import com.wampert.wampert.enums.InspectionStatus;
import com.wampert.wampert.enums.InspectionType;
import com.wampert.wampert.enums.RepairStatus;
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
public class InspectionResponse {
    private String id;
    private String inspectionReference;
    private String bookingId;
    private String carId;
    private String userId;
    private String inspectorId;
    private InspectionType inspectionType;
    private LocalDateTime dateOfInspection;
    private InspectionStatus inspectionStatus;
    private CarCondition condition;
    private Boolean isDamaged;
    private List<String> damagedParts;
    private Double damageCost;
    private RepairStatus repairStatus;
    private LocalDateTime repairCompletedAt;
    private String additionalNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
