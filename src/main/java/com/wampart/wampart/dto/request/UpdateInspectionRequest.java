package com.wampart.wampart.dto.request;

import com.wampart.wampart.enums.CarCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInspectionRequest {
    private CarCondition carCondition;

    private Boolean isDamage;

    private Boolean isDamaged;

    private CarCondition condition;

    private String inspectionComment;

    private String customerResponse;

    private List<String> damagedPhotos;

    private Boolean isDamageChargeRequired;

    private Double damageChargeAmount;
}
