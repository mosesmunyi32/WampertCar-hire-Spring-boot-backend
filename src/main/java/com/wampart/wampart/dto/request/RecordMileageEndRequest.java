package com.wampart.wampart.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordMileageEndRequest {

    @NotNull(message = "Mileage end is required")
    private String mileageEnd;
}
