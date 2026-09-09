package com.wampert.wampert.dto.request;

import com.wampert.wampert.enums.CarCondition;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostInspectionRequest {

    @NotBlank(message = "Booking ID is required")
    private String bookingId;


    private CarCondition condition;

    private Boolean isDamaged;

    @NotBlank(message = "Inspection comment is required")
    private String inspectionComment;

    private List<String> damagedPhotos;

    private Boolean isDamageChargeRequired;
    private Double damageChargeAmount;
}
