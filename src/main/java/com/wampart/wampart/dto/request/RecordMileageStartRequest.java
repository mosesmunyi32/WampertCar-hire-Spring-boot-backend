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
public class RecordMileageStartRequest {

    @NotNull(message = "Mileage start is required")
    private String mileageStart;
}
