package com.wampart.wampart.controller;

import com.wampart.wampart.dto.request.ApproveBookingExtensionRequest;
import com.wampart.wampart.dto.request.BookingExtensionRequest;
import com.wampart.wampart.dto.response.BookingExtensionResponse;
import com.wampart.wampart.service.BookingExtensionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookingExtensionController {
    private final BookingExtensionService bookingExtensionService;

    @PostMapping("/bookings/extensions")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingExtensionResponse> createBookingExtension(@Valid @RequestBody BookingExtensionRequest request) {
        return ResponseEntity.ok(bookingExtensionService.requestExtension(request));

    }

    @GetMapping("/bookings/extensions/my-extensions")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<BookingExtensionResponse>> getMyBookingExtensions() {
        return ResponseEntity.ok(bookingExtensionService.getMyExtensions());
    }

    @PatchMapping("/admin/extensions/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<BookingExtensionResponse> approveOrRejectBooking(@Valid @RequestBody ApproveBookingExtensionRequest request) {
        return ResponseEntity.ok(bookingExtensionService.approveOrRejectExtension(request));
    }

    @GetMapping("/admin/extensions")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<BookingExtensionResponse>> getAllBookingExtensions() {
        return ResponseEntity.ok(bookingExtensionService.getAllExtensions());
    }

    @GetMapping("/admin/bookings/{bookingId}/extensions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<BookingExtensionResponse>> getExtensionByBookingId(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingExtensionService.getExtensionsByBookingId(bookingId));
    }

}
