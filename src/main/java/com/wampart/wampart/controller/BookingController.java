package com.wampart.wampart.controller;


import com.wampart.wampart.dto.request.*;
import com.wampart.wampart.dto.response.AdminBookingResponse;
import com.wampart.wampart.dto.response.BookingHistoryResponse;
import com.wampart.wampart.dto.response.CustomerBookingResponse;
import com.wampart.wampart.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    //================Customer End Points=====//
    @PostMapping("/bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerBookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping("/bookings/my-bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<CustomerBookingResponse>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings());
    }

    @GetMapping("/bookings/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerBookingResponse> getBookingById(@PathVariable String id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PatchMapping("/bookings/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerBookingResponse> cancelBooking(@PathVariable String id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    //==========Admin Methods=========================//
    @GetMapping("/admin/bookings")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN' )")
    public ResponseEntity<List<AdminBookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PatchMapping("/admin/bookings/{id}/mileage-start")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AdminBookingResponse> recordMileageStart(@PathVariable String id, @Valid @RequestBody RecordMileageStartRequest request) {
        return ResponseEntity.ok( bookingService.recordMileageStartAndSetConfirmed(id, request));

    }

    @PatchMapping("/admin/bookings/{id}/mileage-end")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN' )")
    public ResponseEntity<AdminBookingResponse> recordMileageEnd(@PathVariable String id, @Valid @RequestBody RecordMileageEndRequest request) {
        return ResponseEntity.ok(bookingService.recordMileageEndAndSetCompleted(id, request));
    }



    @GetMapping("/admin/bookings/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN' )")
    public ResponseEntity<AdminBookingResponse> getBookingByIdForAdmin(@PathVariable String id) {
        return ResponseEntity.ok(bookingService.getBookingByIdForAdmin(id));
    }

    @PatchMapping ("/admin/bookings/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<AdminBookingResponse> approveOrRejectBooking(@PathVariable String id, @Valid @RequestBody ApproveBookingRequest request){
        return ResponseEntity.ok(bookingService.approveOrRejectBooking(id,request));
    }

    @PostMapping("/admin/bookings/create-for-customer")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AdminBookingResponse> createBooking(@Valid @RequestBody AdminBookingRequest request ) {
        return ResponseEntity.ok(bookingService.createBookingForCustomer(request));
    }

    //================= Booking history endpoints======================

    @GetMapping("/admin/cars/{carId}/booking-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<BookingHistoryResponse>> getBookingHistoryByCar(@PathVariable String carId ){
        return ResponseEntity.ok(bookingService.getBookingHistoryByCar(carId));
    }

    @GetMapping("/admin/customers/{userId}/booking-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<BookingHistoryResponse>> getBookingHistoryByUser(@PathVariable String userId) {
        return ResponseEntity.ok(bookingService.getBookingHistoryByUser(userId));
    }

    //users get their booking history
    @GetMapping("/bookings/my-booking-history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<BookingHistoryResponse>> getMyBookingHistory() {
        return ResponseEntity.ok(bookingService.getMyBookingHistory());
    }

    //swap car
    @PatchMapping("/admin/bookings/{bookingId}/reassign")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'CUSTOMER') ")
    public ResponseEntity<AdminBookingResponse> reassignBooking (@PathVariable String bookingId, @Valid @RequestBody ReassignBookingRequest request) {
        return ResponseEntity.ok(bookingService.reassignBooking(bookingId, request));
    }


}
