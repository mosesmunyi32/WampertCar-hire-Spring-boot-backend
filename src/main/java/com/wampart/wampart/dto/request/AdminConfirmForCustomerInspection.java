package com.wampart.wampart.dto.request;

import com.wampart.wampart.enums.CustomerResponse;
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

    private CustomerResponse adminResponseForCustomer;

}
