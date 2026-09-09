package com.wampert.wampert.dto.request;

import com.wampert.wampert.enums.ExtensionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveBookingExtensionRequest {

    private String extensionId;

    @NotNull(message = "Extension status is required")
    private ExtensionStatus extensionStatus;


    private String adminNote;
}