package com.wampart.wampart.dto.response;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookedDatesResponse {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
