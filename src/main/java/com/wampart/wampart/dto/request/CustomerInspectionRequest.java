package com.wampart.wampart.dto.request;

import com.wampart.wampart.enums.CustomerResponse;
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
    private CustomerResponse customerResponse;

    private String customerComment;


}
