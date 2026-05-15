package com.wampart.wampart.dto.request;

import com.wampart.wampart.enums.ExtensionStatus;
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

    @NotBlank(message = "Extension ID is required")
    private String extensionId;

    @NotNull(message = "Extension status is required")
    private ExtensionStatus extensionStatus;

    @NotNull(message = "Please provide an extension note")
    private String adminNote;
}