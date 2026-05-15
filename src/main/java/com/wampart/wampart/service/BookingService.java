package com.wampart.wampart.service;


import com.wampart.wampart.dto.request.*;
import com.wampart.wampart.dto.response.AdminBookingResponse;
import com.wampart.wampart.dto.response.CustomerBookingResponse;
import com.wampart.wampart.enums.BookingStatus;
import com.wampart.wampart.exception.ResourceNotFoundException;
import com.wampart.wampart.model.BookingEntity;
import com.wampart.wampart.model.CarEntity;
import com.wampart.wampart.model.UserEntity;
import com.wampart.wampart.repositoty.BookingRepository;
import com.wampart.wampart.repositoty.CarRepository;
import com.wampart.wampart.repositoty.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;


    //===============================Helper Methods===============/

    //Get Current Logged-in user
    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User not found"));

    }


    private String generateBookingReference() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = bookingRepository.count() + 1;
        return String.format("WAMP-%s-%03d", date, count);
    }

    //check if the car is available for the requested dates

    private boolean isCarAvailableForDates(String carId, LocalDateTime startDate, LocalDateTime endDate ) {
        List<BookingEntity> overlappingBookings = bookingRepository.findOverlappingBookings(carId, startDate, endDate);
        return overlappingBookings.isEmpty();
    }


    //=================================Customer Methods=============//

    public CustomerBookingResponse createBooking(BookingRequest request) {
        UserEntity customer = getCurrentUser();

        CarEntity car = carRepository.findById(request.getCarId()).orElseThrow(()-> new ResourceNotFoundException("Car not found"));

        if(!car.getIsAvailable() ) {
            throw new RuntimeException("Car is not available for booking");
        }


        if(!car.getInsuranceExpiryDate().isAfter(LocalDateTime.now()) ) {
            throw new RuntimeException("Insurance is not active for this car");
        }

        if(!isCarAvailableForDates(request.getCarId(), request.getStartDate(), request.getEndDate())) {
            throw new RuntimeException("Car is not available for the requested dates");
        }

        long numberOfDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());

        if(numberOfDays < 2 ) {
            throw new RuntimeException("cars are available for more than one day");
        }

        double bookingCost = numberOfDays * car.getPricePerDay();

        String bookingReference = generateBookingReference();

        BookingEntity booking = BookingEntity.builder()
                .bookingReference(bookingReference)
                .userId(customer.getId())
                .carId(car.getId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .bookingCost(bookingCost)
                .travelDestination(request.getTravelDestination())
                .numberOfDays((int) numberOfDays )
                .pricePerDay(car.getPricePerDay())
                .discount(0.0)
                .extendedDays(0)
                .extendedDaysCost(0.0)
                .bookingStatus(BookingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(car.getUpdatedAt())
                .customerNote(request.getCustomerNote())
                .build();
        BookingEntity savedBooking = bookingRepository.save(booking);
        return mapToCustomerBookingResponse(savedBooking);

    }


    public List<CustomerBookingResponse> getMyBookings() {
        UserEntity customer = getCurrentUser();
        return bookingRepository.findByUserId(customer.getId())
                .stream()
                .map(this::mapToCustomerBookingResponse)
                .collect(Collectors.toList());
    }

    public CustomerBookingResponse getBookingById(String id) {
        UserEntity customer = getCurrentUser();
        BookingEntity booking = bookingRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Booking not found"));

        if(!booking.getUserId().equals(customer.getId())) {
            throw new RuntimeException("You are not authorized to view this booking");
        }

        return mapToCustomerBookingResponse(booking);

    }

    public CustomerBookingResponse cancelBooking(String id){
        UserEntity customer = getCurrentUser();

        BookingEntity booking = bookingRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Booking not found"));

        if(!booking.getUserId().equals(customer.getId())) {
            throw new RuntimeException("You are not authorized to cancel this booking");
        }

        if(booking.getBookingStatus().equals(BookingStatus.CANCELLED) || booking.getBookingStatus().equals(BookingStatus.REJECTED)) {
            throw new RuntimeException("Booking cannot be cancelled at this stage")
;
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        BookingEntity updatedBooking = bookingRepository.save(booking);
        return mapToCustomerBookingResponse(updatedBooking);

    }




    CustomerBookingResponse mapToCustomerBookingResponse(BookingEntity booking) {
        return CustomerBookingResponse.builder()
                .id(booking.getId())
                .carId(booking.getCarId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .bookingCost(booking.getBookingCost())
                .bookingStatus(booking.getBookingStatus())
                .travelDestination(booking.getTravelDestination())
                .numberOfDays(booking.getNumberOfDays())
                .pricePerDay(booking.getPricePerDay())
                .discount(booking.getDiscount())
                .customerNote(booking.getCustomerNote())
                .adminNote((booking.getAdminNote()))
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }



    //====================Admin Methods ========================//

    public AdminBookingResponse recordMileageStartAndSetConfirmed(String id, RecordMileageStartRequest request ) {
        BookingEntity booking = bookingRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Booking not found"));


        if(booking.getBookingStatus() != BookingStatus.CONFIRMED) {
           throw new RuntimeException("Booking must be Confirmed before to record mileage");
        }

        booking.setMileageStart(request.getMileageStart());
        BookingEntity updatedBooking = bookingRepository.save(booking);
        return mapToAdminBookingResponse(updatedBooking);
    }

    public AdminBookingResponse recordMileageEndAndSetCompleted(String id, RecordMileageEndRequest request ) {

        BookingEntity booking  = bookingRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Booking not found"));

     if(booking.getBookingStatus() != BookingStatus.CONFIRMED ) {
         throw new RuntimeException("Booking must be Completed before to record mileage");

     }


     booking.setMileageEnd(request.getMileageEnd());

     booking.setBookingStatus(BookingStatus.COMPLETED);

     booking.setActualReturnDate(LocalDateTime.now());
     BookingEntity updatedBooking = bookingRepository.save(booking);

     return mapToAdminBookingResponse(updatedBooking);

    }



//Get all bookings
    public List<AdminBookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToAdminBookingResponse)
                .collect(Collectors.toList());

    }



    //getBooking by id

    public AdminBookingResponse getBookingByIdForAdmin(String id) {
        BookingEntity booking = bookingRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Booking not found"));

        return mapToAdminBookingResponse(booking );

    }

    public AdminBookingResponse createBookingForCustomer( AdminBookingRequest request ) {
        UserEntity admin = getCurrentUser();

        UserEntity customer = userRepository.findByIdNumber(request.getIdNumber()).orElseThrow(()-> new ResourceNotFoundException("User not found with Id: " + request.getIdNumber()));

        CarEntity car = carRepository.findByNumberPlate(request.getNumberPlate()).orElseThrow(()-> new ResourceNotFoundException("Car not found with number plate: " + request.getNumberPlate()));

        if(!car.getIsAvailable() ) {
            throw new RuntimeException("Car is not available for booking");
        }


        if(!car.getInsuranceExpiryDate().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Insurance is not active for this car");
        }
        if(!isCarAvailableForDates(car.getId(), request.getStartDate(), request.getEndDate())) {
            throw new RuntimeException("Car is not available for the requested dates");
        }

        long numberOfDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());

        if(numberOfDays < 2 ) {
            throw new RuntimeException("cars are available for more than one day");
        }

        double bookingCost = numberOfDays * car.getPricePerDay();

        String bookingReference = generateBookingReference();

        BookingEntity booking = BookingEntity.builder()
                .bookingReference(bookingReference)
                .userId(customer.getId())
                .carId(car.getId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .travelDestination(request.getTravelDestination())
                .numberOfDays((int) numberOfDays)
                .pricePerDay(car.getPricePerDay())
                .discount(0.0)
                .bookingCost(bookingCost)
                .extendedDays(0)
                .extendedDaysCost(0.0)
                .bookingStatus(BookingStatus.CONFIRMED)
                .adminNote(request.getAdminNote())
                .approvedBy(admin.getId())
                .build();

        BookingEntity savedBooking = bookingRepository.save(booking);
        return mapToAdminBookingResponse(savedBooking);


    }



    //Approve or Reject a Booking
    public AdminBookingResponse approveOrRejectBooking(String id, ApproveBookingRequest request) {
        UserEntity admin = getCurrentUser();

        BookingEntity booking = bookingRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Booking not found"));

        if(booking.getBookingStatus() != BookingStatus.PENDING ) {
            throw new RuntimeException("Only Pending bookings can be approved or rejected");
        }

        booking.setBookingStatus(request.getBookingStatus());
        booking.setAdminNote(request.getAdminNote());
        booking.setApprovedBy(admin.getId());

       BookingEntity updateBooking =  bookingRepository.save(booking);

       return mapToAdminBookingResponse(updateBooking);

    }


    AdminBookingResponse mapToAdminBookingResponse(BookingEntity booking) {
        return AdminBookingResponse.builder()
                .id(booking.getId())
                .bookingReference(booking.getBookingReference())
                .userId(booking.getUserId())
                .carId(booking.getCarId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .actualReturnDate(booking.getActualReturnDate())
                .travelDestination(booking.getTravelDestination())
                .numberOfDays(booking.getNumberOfDays())
                .pricePerDay(booking.getPricePerDay())
                .bookingCost(booking.getBookingCost())
                .extendedDays(booking.getExtendedDays())
                .extendedDaysCost(booking.getExtendedDaysCost())
                .discount(booking.getDiscount())
                .bookingStatus(booking.getBookingStatus())
                .customerNote(booking.getCustomerNote())
                .adminNote(booking.getAdminNote())
                .mileageStart(booking.getMileageStart())
                .mileageEnd(booking.getMileageEnd())
                .approvedBy(booking.getApprovedBy())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }





}
