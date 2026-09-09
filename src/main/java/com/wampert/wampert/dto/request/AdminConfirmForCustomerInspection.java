package com.wampert.wampert.dto.request;

import com.wampert.wampert.enums.CustomerResponseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminConfirmForCustomerInspection {

    private CustomerResponseStatus adminResponseForCustomer;

}
