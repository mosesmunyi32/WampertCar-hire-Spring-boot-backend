package com.wampart.wampart.service;


import com.wampart.wampart.dto.request.CreatePostInspectionRequest;
import com.wampart.wampart.dto.request.CreatePreInspectionRequest;
import com.wampart.wampart.dto.request.CustomerInspectionRequest;
import com.wampart.wampart.dto.request.UpdateInspectionRequest;
import com.wampart.wampart.dto.response.AdminInspectionResponse;
import com.wampart.wampart.dto.response.CustomerInspectionResponse;
import com.wampart.wampart.enums.BookingStatus;
import com.wampart.wampart.enums.CustomerResponse;
import com.wampart.wampart.enums.InspectionStatus;
import com.wampart.wampart.enums.InspectionType;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InspectionService {
    private final InspectionRepository inspectionRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;


    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
    }

    private String generateInspectionReference() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = inspectionRepository.count() + 1;
        return String.format("INSP-%s-%03d", date, count);
    }



    public List<AdminInspectionResponse> getInspectionsForBooking(String bookingId) {
        bookingRepository.findById(bookingId).orElseThrow(()-> new RuntimeException("Booking not found"));

        return inspectionRepository.findByBookingId(bookingId)
                .stream()
                .map(this::mapToAdminInspectionResponse)
                .collect(Collectors.toList());

    }

    public List<CustomerInspectionResponse> getMyInspectionForBooking(String bookingId) {
        UserEntity customer = getCurrentUser();
        BookingEntity booking = bookingRepository.findById(bookingId).orElseThrow(()-> new RuntimeException("Booking not found"));

        if(!booking.getUserId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("You are not authorized to view this inspection");
        }
        return inspectionRepository.findByBookingId(bookingId)
                .stream()
                .map(this::mapToCustomerInspectionResponse)
                .collect(Collectors.toList());
    }



    public AdminInspectionResponse createPreInspection(CreatePreInspectionRequest request) {
        BookingEntity booking = bookingRepository.findById(request.getBookingId()).orElseThrow(()-> new RuntimeException("Booking not found"));
        UserEntity customer = userRepository.findById(booking.getUserId()).orElseThrow(()-> new RuntimeException("Customer not found"));
        CarEntity car = carRepository.findById(booking.getCarId()).orElseThrow(()-> new RuntimeException("Car not found"));





        if(booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Pre-Inspection can only be done on a Confirmed booking");
        }

        inspectionRepository.findByBookingIdAndInspectionType(booking.getId(), InspectionType.PRE_INSPECTION )
                .ifPresent(existing -> {
                    throw new BadRequestException("Pre-Inspection already exists for this booking");
                });
        InspectionEntity inspection = InspectionEntity.builder()
                .inspectionReference(generateInspectionReference())
                .bookingId(booking.getId())
                .carNumberPlate(car.getNumberPlate())
                .carBrand(car.getBrand())
                .carModel(car.getModel())
                .condition(request.getCondition())
                .userFirstName(customer.getFirstName())
                .userLastName(customer.getLastName())
                .userIdNumber(customer.getIdNumber())
                .userPhoneNumber(customer.getPhoneNumber())
                .inspectionType(InspectionType.PRE_INSPECTION)
                .inspectionStatus(InspectionStatus.PENDING)
                .customerResponse(CustomerResponse.PENDING)
                .isDamaged(request.getIsDamaged())
                .damagedPhotos(request.getDamagedPhotos())
                .inspectionComment(request.getInspectionComment())
                .inspectionDate(LocalDateTime.now())
                .build();

        InspectionEntity savedInspection = inspectionRepository.save(inspection);
        return mapToAdminInspectionResponse(savedInspection);


    }


    public CustomerInspectionResponse customerRespondToInspection(String InspectionId, CustomerInspectionRequest request) {
        UserEntity customer = getCurrentUser();

        InspectionEntity inspection = inspectionRepository.findById(InspectionId).orElseThrow(()-> new RuntimeException("Inspection not found"));

        if(inspection.getCustomerResponse()  != CustomerResponse.PENDING) {
            throw new BadRequestException("Customer has already responded to this inspection");
        }

        BookingEntity booking = bookingRepository.findById(inspection.getBookingId()).orElseThrow(()-> new RuntimeException("Booking not found"));
        if(!booking.getUserId().equals(customer.getId())) {
            throw new RuntimeException("You are not authorized to respond to this inspection");
        }

        if(request.getCustomerResponse() == CustomerResponse.REJECTED
        && (request.getCustomerComment() == null || request.getCustomerComment().isBlank() ))
        {
            throw new BadRequestException("You must provide a comment why you rejected this inspection");

        }

        inspection.setCustomerResponse(request.getCustomerResponse());
        inspection.setCustomerComment(request.getCustomerComment());
        return mapToCustomerInspectionResponse(inspectionRepository.save(inspection));


    }








    public AdminInspectionResponse createPostInspection(CreatePostInspectionRequest request) {
        BookingEntity booking = bookingRepository.findById(request.getBookingId()).orElseThrow(()-> new RuntimeException("Booking not found"));

        if(booking.getBookingStatus() != BookingStatus.COMPLETED){
            throw new BadRequestException("Post-Inspection can only be done on a Completed booking, save the end mileage to continue..");
        }

        InspectionEntity preInspection = inspectionRepository.findByBookingIdAndInspectionType(booking.getId(), InspectionType.PRE_INSPECTION ).orElseThrow(()-> new BadRequestException("Pre-Inspection must be completed before post-inspection"));

        if(preInspection.getCustomerResponse() != CustomerResponse.CONFIRMED) {
            throw new BadRequestException("customer must confirm the pre-inspection before post inspection");
        }
        inspectionRepository.findByBookingIdAndInspectionType(booking.getId(), InspectionType.POST_INSPECTION ).ifPresent(existing -> {
            throw new BadRequestException("Post-Inspection already exists for this booking");
        });

        CarEntity car = carRepository.findById(booking.getCarId()).orElseThrow(()-> new RuntimeException("Car not found"));

        UserEntity customer = userRepository.findById(booking.getUserId()).orElseThrow(()-> new RuntimeException("Customer not found"));

        InspectionEntity inspection = InspectionEntity.builder()
                .inspectionReference(generateInspectionReference())
                .bookingId(booking.getId())
                .carNumberPlate(car.getNumberPlate())
                .carModel(car.getModel())
                .carBrand(car.getBrand())
                .userFirstName(customer.getFirstName())
                .userLastName(customer.getLastName())
                .userPhoneNumber(customer.getPhoneNumber())
                .userIdNumber(customer.getIdNumber())
                .inspectionType(InspectionType.POST_INSPECTION)
                .inspectionStatus(InspectionStatus.COMPLETED)
                .condition(request.getCondition())
                .isDamaged(request.getIsDamaged())
                .inspectionComment(request.getInspectionComment())
                .damagedPhotos(request.getDamagedPhotos())
                .inspectionDate(booking.getActualReturnDate() != null ? booking.getActualReturnDate() : booking.getEndDate())
                .customerResponse(CustomerResponse.PENDING)
                .isDamageChargeRequired(request.getIsDamageChargeRequired())
                .damageChargeAmount(request.getDamageChargeAmount())
                .build();

        InspectionEntity savedInspection = inspectionRepository.save(inspection);
        return mapToAdminInspectionResponse(savedInspection);

    }


    public AdminInspectionResponse adminUpdateInspection(String inspectionId, UpdateInspectionRequest request) {
        InspectionEntity inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection not found"));

        inspection.setCondition(request.getCondition());
        inspection.setIsDamaged(request.getIsDamaged());
        inspection.setInspectionComment(request.getInspectionComment());
        inspection.setDamagedPhotos(request.getDamagedPhotos());
        inspection.setIsDamageChargeRequired(request.getIsDamageChargeRequired());
        inspection.setDamageChargeAmount(request.getDamageChargeAmount());
        inspection.setCustomerResponse(CustomerResponse.PENDING);

        InspectionEntity updated = inspectionRepository.save(inspection);
        return mapToAdminInspectionResponse(updated);
    }








    private AdminInspectionResponse mapToAdminInspectionResponse(InspectionEntity inspection) {
        return AdminInspectionResponse.builder()
                .id(inspection.getId())
                .inspectionReference(inspection.getInspectionReference())
                .bookingId(inspection.getBookingId())
                .carNumberPlate(inspection.getCarNumberPlate())
                .carModel(inspection.getCarModel())
                .carBrand(inspection.getCarBrand())
                .userFirstName(inspection.getUserFirstName())
                .userLastName(inspection.getUserLastName())
                .userPhoneNumber(inspection.getUserPhoneNumber())
                .userIdNumber(inspection.getUserIdNumber())
                .inspectionType(inspection.getInspectionType())
                .customerResponse(inspection.getCustomerResponse())
                .customerComment(inspection.getCustomerComment())
                .isDamageChargeRequired(inspection.getIsDamageChargeRequired())
                .damageChargeAmount(inspection.getDamageChargeAmount())
                .inspectionStatus(inspection.getInspectionStatus())
                .condition(inspection.getCondition())
                .isDamaged(inspection.getIsDamaged())
                .damagedPhotos(inspection.getDamagedPhotos())
                .inspectionComment(inspection.getInspectionComment())
                .dateOfInspection(inspection.getInspectionDate())
                .createdAt(inspection.getCreatedAt())
                .updatedAt(inspection.getUpdatedAt())
                .build();


    }


    private CustomerInspectionResponse mapToCustomerInspectionResponse(InspectionEntity inspection) {
        return CustomerInspectionResponse.builder()
                .id(inspection.getId())
                .inspectionReference(inspection.getInspectionReference())
                .bookingId(inspection.getBookingId())
                .carNumberPlate(inspection.getCarNumberPlate())
                .carModel(inspection.getCarModel())
                .carBrand(inspection.getCarBrand())
                .inspectionType(inspection.getInspectionType())
                .inspectionStatus(inspection.getInspectionStatus())
                .customerResponse(inspection.getCustomerResponse())
                .customerComment(inspection.getCustomerComment())
                .isDamageChargeRequired(inspection.getIsDamageChargeRequired())
                .damageChargeAmount(inspection.getDamageChargeAmount())
                .condition(inspection.getCondition())
                .isDamaged(inspection.getIsDamaged())
                .damagedPhotos(inspection.getDamagedPhotos())
                .inspectionComment(inspection.getInspectionComment())
                .dateOfInspection(inspection.getInspectionDate())
                .createdAt(inspection.getCreatedAt())
                .updatedAt(inspection.getUpdatedAt())
                .build();

    }
}
