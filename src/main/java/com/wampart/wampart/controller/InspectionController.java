package com.wampart.wampart.controller;


import com.wampart.wampart.dto.request.*;
import com.wampart.wampart.dto.response.AdminInspectionResponse;
import com.wampart.wampart.dto.response.CustomerInspectionResponse;
import com.wampart.wampart.service.InspectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InspectionController {
    private final InspectionService inspectionService;


    @PostMapping("/admin/inspections/pre-inspection")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AdminInspectionResponse> CreatePreInspection(@Valid @RequestBody CreatePreInspectionRequest request ) {
        return ResponseEntity.ok(inspectionService.createPreInspection(request));

    }

    //customer confirms pre-inspection
    @PatchMapping("/inspections/{inspectionId}/respond")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'SUPER_ADMIN')")
    public ResponseEntity<CustomerInspectionResponse> confirmPreInspection(
            @PathVariable String inspectionId,
            @Valid @RequestBody CustomerInspectionRequest request
    ) {
        return ResponseEntity.ok(
                inspectionService.customerRespondToInspection(inspectionId, request)
        );
    }

    @PatchMapping("/admin/bookings/{bookingId}/inspection/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AdminInspectionResponse> confirmForCustomer(@PathVariable String bookingId) {
        return ResponseEntity.ok(
                inspectionService.adminConfirmForCustomerInspection(bookingId)
        );
    }

    //admin creates post-inspection
    @PostMapping("/admin/inspections/post-inspection")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AdminInspectionResponse> createPostInspection(@Valid @RequestBody CreatePostInspectionRequest request) {
        return ResponseEntity.ok(inspectionService.createPostInspection(request));
    }

    //admin getting all inspections for a booking
    @GetMapping("/admin/bookings/{bookingId}/inspections")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<AdminInspectionResponse>> getAllInspectionsForBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(inspectionService.getInspectionsForBooking(bookingId));

    }

    @PatchMapping("/admin/inspections/{inspectionId}/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AdminInspectionResponse> updateInspection(
            @PathVariable String inspectionId,
            @Valid @RequestBody UpdateInspectionRequest request) {
        return ResponseEntity.ok(inspectionService.adminUpdateInspection(inspectionId, request));
    }

    //customer can get inspection for their booking
    @GetMapping("/bookings/{bookingId}/inspections")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<CustomerInspectionResponse>> getInspectionsForBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(inspectionService.getMyInspectionForBooking(bookingId));
    }



}
