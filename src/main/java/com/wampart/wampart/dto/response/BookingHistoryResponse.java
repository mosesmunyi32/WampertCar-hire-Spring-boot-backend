package com.wampart.wampart.dto.response;


import com.wampart.wampart.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingHistoryResponse {
    private String bookingReference;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BookingStatus bookingStatus;
    private Integer numberOfDays;
    private double bookingCost;

    private String carBrand;
    private String carModel;
    private Integer carYear;
    private String numberPlate;

    private String customerFirstName;
    private String customerLastName;
    private String customerPhoneNumber;
    private String customerEmail;

    private String inspectionComment;
    private Boolean carWasDamaged;

    private LocalDateTime createdAt;


}
