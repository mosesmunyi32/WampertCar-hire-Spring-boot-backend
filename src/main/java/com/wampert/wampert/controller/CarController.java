package com.wampert.wampert.controller;


import com.wampert.wampert.dto.request.CarRequest;
import com.wampert.wampert.dto.response.AdminCarResponse;
import com.wampert.wampert.dto.response.BookedDatesResponse;
import com.wampert.wampert.dto.response.CustomerCarResponse;
import com.wampert.wampert.dto.response.ToggleAvailabilityResponse;
import com.wampert.wampert.service.BookingService;
import com.wampert.wampert.service.CarService;
import com.wampert.wampert.service.ToggleAvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;
    private final ToggleAvailabilityService toggleAvailabilityService;
    private final BookingService bookingService;

    @PostMapping("/admin/addcars")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AdminCarResponse> addCar(@Valid @RequestBody CarRequest request) {
        return ResponseEntity.ok(carService.addCar(request));
    }

    @GetMapping("/admin/cars")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<AdminCarResponse>> getAllCarsForAdmin() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping("/cars")
//    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<CustomerCarResponse>>  getAllAvailableCars() {
        return ResponseEntity.ok(carService.getAllAvailableCars());

    }

    @GetMapping("/admin/cars/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AdminCarResponse> getCarById(@PathVariable String id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PatchMapping("/admin/cars/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AdminCarResponse> updateCarById(@PathVariable String id, @RequestBody CarRequest request) {
        return ResponseEntity.ok(carService.updateCar(id, request));
    }


    @GetMapping("/cars/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<CustomerCarResponse> getCarByIdForCustomer(@PathVariable String id ) {
        return ResponseEntity.ok(carService.getCarByIdForCustomer(id));
    }

    @GetMapping("/cars/{carId}/booked-dates")
    public ResponseEntity<List<BookedDatesResponse>> getBookedDatesForCar(@PathVariable String carId) {
        return ResponseEntity.ok(bookingService.getBookedDatesForCar(carId));
    }

//    @PutMapping("/cars/admin/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
//    public ResponseEntity<AdminCarResponse> addCar(@PathVariable String id, @Valid @RequestBody CarRequest request) {
//        return ResponseEntity.ok(carService.updateCar(id,request));
//    }

    @DeleteMapping("/admin/cars/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> deleteCar(@PathVariable String id) {
        return ResponseEntity.ok(carService.deleteCar(id));
    }

    @PatchMapping("/admin/cars/{id}/availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ToggleAvailabilityResponse> toggleCarAvailability(@PathVariable String id) {
        return ResponseEntity.ok(toggleAvailabilityService.toggleAvailability(id));

    }







}
