package com.wampart.wampart.service;


import com.wampart.wampart.dto.request.*;
import com.wampart.wampart.dto.response.AdminBookingResponse;
import com.wampart.wampart.dto.response.BookedDatesResponse;
import com.wampart.wampart.dto.response.BookingHistoryResponse;
import com.wampart.wampart.dto.response.CustomerBookingResponse;
import com.wampart.wampart.enums.BookingStatus;
import com.wampart.wampart.enums.InspectionType;
import com.wampart.wampart.enums.Role;
import com.wampart.wampart.exception.BadRequestException;
import com.wampart.wampart.exception.ResourceNotFoundException;
import com.wampart.wampart.model.BookingEntity;
import com.wampart.wampart.model.CarEntity;
import com.wampart.wampart.model.InspectionEntity;
import com.wampart.wampart.model.UserEntity;
import com.wampart.wampart.repositoty.BookingRepository;
import com.wampart.wampart.repositoty.CarRepository;
import com.wampart.wampart.repositoty.InspectionRepository;
import com.wampart.wampart.repositoty.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final WhatsAppService whatsAppService;
    private final PdfService pdfService;
    private final InspectionRepository inspectionRepository;

    @Value("${booking.pending-expiry-minutes:10}")
    private long pendingExpiryMinutes;




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


    //========EXPIRY

    @Scheduled(fixedRateString = "${booking.expiry-check-interval-ms:60000}")
    public void expireStalePnedingBookings() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(pendingExpiryMinutes);
        List<BookingEntity> staleBookings = bookingRepository.findByBookingStatusAndCreatedAtBefore(BookingStatus.PENDING, cutoff);

        if(staleBookings.isEmpty()) {
            return;
        }

        for (BookingEntity booking: staleBookings ) {
            booking.setBookingStatus(BookingStatus.EXPIRED);

        }

        bookingRepository.saveAll(staleBookings);
        log.info("Expired {} pending booking(s) older than {} minutes", staleBookings.size(), pendingExpiryMinutes);
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

        try {
            whatsAppService.notifyAdminNewBooking(
                    savedBooking.getBookingReference(),
                    customer.getFirstName() + " " + customer.getLastName(),
                    car.getBrand() + " " + car.getModel(),
                    savedBooking.getStartDate().toString(),
                    savedBooking.getEndDate().toString(),
                    savedBooking.getBookingCost()
            ) ;
            } catch (Exception e) {
            log.warn("Failed to send whatsapp notification {}", e.getMessage());

        }



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


    // Edit a PENDING or CONFIRMED booking. Usable by the owning customer or any admin.
    public CustomerBookingResponse updateBooking(String id, UpdateBookingRequest request) {
        UserEntity currentUser = getCurrentUser();

        BookingEntity booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.SUPER_ADMIN;
        if (!isAdmin && !booking.getUserId().equals(currentUser.getId())) {
            throw new BadRequestException("You are not authorized to edit this booking");
        }

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Only pending bookings can be edited");
        }

        LocalDateTime newStart = request.getStartDate() != null ? request.getStartDate() : booking.getStartDate();
        LocalDateTime newEnd = request.getEndDate() != null ? request.getEndDate() : booking.getEndDate();

        boolean datesChanged = !newStart.equals(booking.getStartDate()) || !newEnd.equals(booking.getEndDate());

        if (datesChanged) {
            if (!newEnd.isAfter(newStart)) {
                throw new BadRequestException("End date must be after start date");
            }

            long numberOfDays = ChronoUnit.DAYS.between(newStart, newEnd);
            if (numberOfDays < 2) {
                throw new BadRequestException("A booking must be for more than one day");
            }

            // Re-check availability for the new dates, ignoring this booking itself
            boolean conflict = bookingRepository
                    .findOverlappingBookings(booking.getCarId(), newStart, newEnd)
                    .stream()
                    .anyMatch(b -> !b.getId().equals(booking.getId()));
            if (conflict) {
                throw new BadRequestException("Car is not available for the requested dates");
            }

            booking.setStartDate(newStart);
            booking.setEndDate(newEnd);
            booking.setNumberOfDays((int) numberOfDays);
            booking.setBookingCost(numberOfDays * booking.getPricePerDay());
        }

        if (request.getTravelDestination() != null) {
            booking.setTravelDestination(request.getTravelDestination());
        }

        BookingEntity updated = bookingRepository.save(booking);
        return mapToCustomerBookingResponse(updated);
    }




    CustomerBookingResponse mapToCustomerBookingResponse(BookingEntity booking) {

        String approvedByName = null;

        if(booking.getApprovedBy() != null ) {

            approvedByName = userRepository.findById(booking.getApprovedBy())
                    .map(UserEntity::getFirstName)
                    .orElse(null);
        }

        LocalDateTime expiresAt = null;
        if(booking.getBookingStatus() == BookingStatus.PENDING && booking.getCreatedAt() != null ) {
            expiresAt = booking.getCreatedAt().plusMinutes(pendingExpiryMinutes);
        }

        return CustomerBookingResponse.builder()
                .id(booking.getId())
                .carId(booking.getCarId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .bookingCost(booking.getBookingCost())
                .bookingStatus(booking.getBookingStatus())
                .expiresAt(expiresAt)
                .travelDestination(booking.getTravelDestination())
                .numberOfDays(booking.getNumberOfDays())
                .pricePerDay(booking.getPricePerDay())
                .discount(booking.getDiscount())
                .customerNote(booking.getCustomerNote())
                .adminNote((booking.getAdminNote()))
                .approvedByName(approvedByName)
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


    //get booked dates



    public List<BookedDatesResponse> getBookedDatesForCar(String carId) {

        return bookingRepository.findActiveBookingsDatesByCar(carId)
                .stream()
                .map(b -> BookedDatesResponse.builder()
                        .startDate(b.getStartDate())
                        .endDate(b.getEndDate())
                        .build())
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
        if(customer.getIsVerified() == false) {
            throw new RuntimeException("Customer is not verified, verify the customer before booking");
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

        try {
            String receiptUrl = pdfService.generateAndStoreBookingReceipt(savedBooking, customer, car);
            whatsAppService.notifyCustomerBookingConfirmed(
                    customer.getPhoneNumber(),
                    customer.getFirstName() + " " + customer.getLastName(),
                    savedBooking.getBookingReference(),
                    car.getBrand() + " " + car.getModel(),
                    savedBooking.getStartDate().toString(),
                    savedBooking.getEndDate().toString(),
                    savedBooking.getBookingCost(),
                    receiptUrl
            );
        } catch (Exception e) {
            log.warn("Failed to generate receipt or send WhatsApp: {}", e.getMessage());
        }

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

       BookingEntity updatedBooking =  bookingRepository.save(booking);

        if (request.getBookingStatus() == BookingStatus.CONFIRMED) {

            // Find customer
            UserEntity customer = userRepository
                    .findById(updatedBooking.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Customer not found"));

            // Find car
            CarEntity car = carRepository
                    .findById(updatedBooking.getCarId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Car not found"));

            // Generate and store receipt
            try {
                String receiptUrl = pdfService
                        .generateAndStoreBookingReceipt(
                                updatedBooking, customer, car);

                // Notify customer via WhatsApp
                whatsAppService.notifyCustomerBookingConfirmed(
                        customer.getPhoneNumber(),
                        customer.getFirstName() + " " + customer.getLastName(),
                        updatedBooking.getBookingReference(),
                        car.getBrand() + " " + car.getModel(),
                        updatedBooking.getStartDate().toString(),
                        updatedBooking.getEndDate().toString(),
                        updatedBooking.getBookingCost(),
                        receiptUrl
                );
            } catch (Exception e) {
                log.warn("Failed to generate receipt or send WhatsApp: {}",
                        e.getMessage());
            }
        }


        return mapToAdminBookingResponse(updatedBooking);

    }

    public AdminBookingResponse adminCancelBooking(String id, AdminCancelBookingRequest request) {
        UserEntity admin = getCurrentUser();

        BookingEntity booking = bookingRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("Booking not found"));

        if(booking.getBookingStatus() !=BookingStatus.PENDING
                && booking.getBookingStatus() != BookingStatus.CONFIRMED ) {
            throw new BadRequestException("Only Pending or Confirmed bookings can be canceled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.setApprovedBy(admin.getId());

        if(request.getReason() != null && !request.getReason().isBlank()) {
            booking.setAdminNote(request.getReason());
        }
        BookingEntity updatedBooking = bookingRepository.save(booking);
        return mapToAdminBookingResponse(updatedBooking);
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



    //=========================Booking History Methods =======================================
    public List<BookingHistoryResponse> getBookingHistoryByCar(String carId) {
        CarEntity car = carRepository.findById(carId).orElseThrow(()-> new ResourceNotFoundException("Car not found"));
        return bookingRepository.findByCarId(carId)
                .stream()
                .map(booking -> mapToBookingHistoryResponse(booking, car, null))
                .collect(Collectors.toList());
    }

    public List<BookingHistoryResponse> getBookingHistoryByUser(String userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found"));

        return bookingRepository.findByUserId(userId)
                .stream()
                .map(booking ->{
                    CarEntity car = carRepository.findById(booking.getCarId()).orElse(null);
                    return mapToBookingHistoryResponse(booking, car, user);
                } )
                .collect(Collectors.toList());
    }


    // users getting their own booking histories
    public List<BookingHistoryResponse> getMyBookingHistory() {
        UserEntity customer = getCurrentUser();
        return bookingRepository.findByUserId(customer.getId())
                .stream()
                .map(booking -> {
                    CarEntity car = carRepository.findById(booking.getCarId()).orElse(null);
                    return mapToBookingHistoryResponse(booking, car, customer);
                } )
                .collect(Collectors.toList());
    }


    public AdminBookingResponse reassignBooking(String id, ReassignBookingRequest request) {
        BookingEntity booking = bookingRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Booking not found"));

        if(booking.getBookingStatus() != BookingStatus.PENDING && booking.getBookingStatus() != BookingStatus.CONFIRMED ) {
            throw new BadRequestException("Booking cannot be reassigned at this stage");
        }

        CarEntity newCar = carRepository.findByNumberPlate(request.getNewCarNumberPlate()).orElseThrow(()-> new ResourceNotFoundException("Car not found with number plate: " + request.getNewCarNumberPlate()));

        if(!newCar.getIsAvailable()) {
            throw new BadRequestException("Car is not available for booking");
        }

        if(!isCarAvailableForDates(newCar.getId(), booking.getStartDate(), booking.getEndDate() )) {
            throw new BadRequestException("Car is not available for the requested dates");
        }

        double newBookingCost = booking.getNumberOfDays() * newCar.getPricePerDay();

        booking.setCarId(newCar.getId());
        booking.setPricePerDay(newCar.getPricePerDay());
        booking.setBookingCost(newBookingCost);
        booking.setAdminNote(request.getAdminNote());
        booking.setUpdatedAt(LocalDateTime.now());

        inspectionRepository.findByBookingIdAndInspectionType(booking.getId(), InspectionType.PRE_INSPECTION)
                .ifPresent(inspectionRepository::delete);

        BookingEntity updatedBooking = bookingRepository.save(booking);
        return mapToAdminBookingResponse(updatedBooking);

    }



    private BookingHistoryResponse mapToBookingHistoryResponse(BookingEntity booking, CarEntity car, UserEntity user) {
        List<InspectionEntity> inspections = inspectionRepository.findByBookingId(booking.getId());
        Optional<InspectionEntity> postInspection = inspections.stream()
                .filter(i -> i.getInspectionType() == InspectionType.POST_INSPECTION )
                .findFirst();

        UserEntity customer = user;
        if(customer == null) {
            customer = userRepository.findById(booking.getUserId()).orElse(null);
        }

        return BookingHistoryResponse.builder()
                .bookingReference(booking.getBookingReference())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .bookingStatus(booking.getBookingStatus())
                .numberOfDays(booking.getNumberOfDays())
                .bookingCost(booking.getBookingCost())
                .carBrand(car !=null ? car.getBrand() : null )
                .carModel(car != null ? car.getModel() : null )
                .carYear(car != null ? car.getYearOfManufacture() : null )
                .numberPlate(car != null ? car.getNumberPlate() : null )
                .customerFirstName( customer !=null ? customer.getFirstName() : null )
                .customerLastName(customer != null ? customer.getLastName() : null )
                .customerEmail(customer != null ? customer.getEmail(): null)
                .customerPhoneNumber(customer !=null ? customer.getPhoneNumber(): null)
                .inspectionComment(postInspection.map(InspectionEntity :: getInspectionComment ).orElse(null))
                .carWasDamaged((postInspection.map(InspectionEntity::getIsDamaged)).orElse(null))
                .createdAt(booking.getCreatedAt())
                .build();


    }

}
