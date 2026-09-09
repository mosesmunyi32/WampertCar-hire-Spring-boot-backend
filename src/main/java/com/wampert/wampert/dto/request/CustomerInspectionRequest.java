package com.wampert.wampert.dto.request;

import com.wampert.wampert.enums.CustomerResponseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInspectionRequest {

    @NotNull(message = "Customer response is required")
    private CustomerResponseStatus customerResponse;

    private String customerComment;


}
