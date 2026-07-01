package com.wampart.wampart.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingRequest {

    // All fields optional: only non-null values are applied (partial update)
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String travelDestination;
}
