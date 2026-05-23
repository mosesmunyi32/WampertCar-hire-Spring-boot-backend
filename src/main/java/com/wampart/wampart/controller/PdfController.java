package com.wampart.wampart.controller;

import com.wampart.wampart.exception.ResourceNotFoundException;
import com.wampart.wampart.model.BookingEntity;
import com.wampart.wampart.model.CarEntity;
import com.wampart.wampart.model.UserEntity;
import com.wampart.wampart.repositoty.BookingRepository;
import com.wampart.wampart.repositoty.CarRepository;
import com.wampart.wampart.repositoty.UserRepository;
import com.wampart.wampart.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PdfController {

    private final PdfService pdfService;
    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    // Generate and store receipt - Admin only
    @PostMapping("/admin/bookings/{bookingId}/generate-receipt")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> generateReceipt(
            @PathVariable String bookingId) {

        // Find booking
        BookingEntity booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + bookingId));

        // Find customer
        UserEntity customer = userRepository
                .findById(booking.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found"));

        // Find car
        CarEntity car = carRepository
                .findById(booking.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Car not found"));

        // Generate and store receipt
        String receiptUrl = pdfService.generateAndStoreBookingReceipt(
                booking, customer, car);

        return ResponseEntity.ok(receiptUrl);
    }

    // Get receipt URL - Customer and Admin
    @GetMapping("/bookings/{bookingId}/receipt")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> getReceiptUrl(
            @PathVariable String bookingId) {

        // Find booking
        BookingEntity booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + bookingId));

        // Check if receipt exists
        if (booking.getReceiptUrl() == null) {
            throw new RuntimeException(
                    "Receipt not yet generated for this booking");
        }

        return ResponseEntity.ok(booking.getReceiptUrl());
    }
}
