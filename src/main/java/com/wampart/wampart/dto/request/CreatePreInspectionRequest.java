package com.wampart.wampart.dto.request;

import com.wampart.wampart.enums.CarCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePreInspectionRequest {

    @NotBlank(message = "Booking ID is required")
    private String bookingId;

    @NotNull(message = "Car condition is required")
    private CarCondition condition;

    @NotNull(message = "isDamaged is required")
    private Boolean isDamaged;

    @NotBlank(message = "Inspection comment is required")
    private String inspectionComment;

    private List<String> damagedPhotos;


}
