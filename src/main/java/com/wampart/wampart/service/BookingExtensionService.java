package com.wampart.wampart.service;

import com.wampart.wampart.dto.request.ApproveBookingExtensionRequest;
import com.wampart.wampart.dto.request.BookingExtensionRequest;
import com.wampart.wampart.dto.response.BookingExtensionResponse;
import com.wampart.wampart.enums.ExtensionStatus;
import com.wampart.wampart.exception.BadRequestException;
import com.wampart.wampart.exception.ResourceNotFoundException;
import com.wampart.wampart.model.BookingEntity;
import com.wampart.wampart.model.BookingExtensionEntity;
import com.wampart.wampart.model.CarEntity;
import com.wampart.wampart.model.UserEntity;
import com.wampart.wampart.repositoty.BookingExtensionRepository;
import com.wampart.wampart.repositoty.BookingRepository;
import com.wampart.wampart.repositoty.CarRepository;
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
public class BookingExtensionService {
    private final BookingExtensionRepository bookingExtensionRepository;
    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final WhatsAppService whatsAppService;
    private final UserRepository userRepository;


    //=========Helper Methods=========//

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));

    }

    private String generateExtensionReference() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = bookingExtensionRepository.count() + 1;
        return String.format("WAMP-EXT-%s-%03d", date, count);

    }

    // Reject an extension if the extra days collide with another booking for the same car
    private void ensureCarFreeForExtension(BookingEntity booking, int requestedDays) {
        LocalDateTime extensionStart = booking.getEndDate();
        LocalDateTime extensionEnd = booking.getEndDate().plusDays(requestedDays);

        boolean conflict = bookingRepository
                .findOverlappingBookings(booking.getCarId(), extensionStart, extensionEnd)
                .stream()
                .anyMatch(b -> !b.getId().equals(booking.getId()));

        if (conflict) {
            throw new BadRequestException("This car has been booked in the days you want to extend to");
        }
    }

    public BookingExtensionResponse requestExtension(BookingExtensionRequest request) {
        UserEntity user = getCurrentUser();

        BookingEntity booking = bookingRepository.findById(request.getBookingId()).orElseThrow(()-> new RuntimeException("Booking not found"));

        if(!booking.getUserId().equals(user.getId())) {
            throw new BadRequestException("You are not authorized to extend this booking");
        }

        if(!booking.getBookingStatus().name().equals("CONFIRMED")) {
            throw new BadRequestException("Only active bookings can be extended");
        }

        List<BookingExtensionEntity> pendingExtensions = bookingExtensionRepository.findByBookingIdAndExtensionStatus(booking.getId(), ExtensionStatus.PENDING );

        if(!pendingExtensions.isEmpty()) {
            throw new BadRequestException("You have already requested an extension for this booking");
        }

        CarEntity car = carRepository.findById(booking.getCarId()).orElseThrow(()-> new RuntimeException("Car not found"));

        ensureCarFreeForExtension(booking, request.getRequestedDays());

        double extensionCost = request.getRequestedDays() * car.getPricePerDay();

        BookingExtensionEntity extension = BookingExtensionEntity.builder()
                .extensionReference(generateExtensionReference())
                .bookingId(booking.getId())
                .requestedDays(request.getRequestedDays())
                .extensionCost(extensionCost)
                .extensionStatus(ExtensionStatus.PENDING)
                .customerNote(request.getCustomerNote())
                .requestedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        BookingExtensionEntity savedBookingExtension = bookingExtensionRepository.save(extension);
        return mapToResponse(savedBookingExtension);



    }


    public BookingExtensionResponse cancelExtension(String extensionReference) {
        return null;
    }

    public List<BookingExtensionResponse> getMyExtensions() {
        UserEntity customer = getCurrentUser();

        return bookingExtensionRepository.findByUserId(customer.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

    }


    //============================ Admin Methods ======================
    public BookingExtensionResponse approveOrRejectExtension(ApproveBookingExtensionRequest request) {
        UserEntity admin = getCurrentUser();

        BookingExtensionEntity extension = bookingExtensionRepository.findById(request.getExtensionId()).orElseThrow(()-> new RuntimeException("Extension not found"));

        if(extension.getExtensionStatus() != ExtensionStatus.PENDING ) {
            throw new BadRequestException("Only Pending extensions can be approved or rejected");
        }
        extension.setExtensionStatus(request.getExtensionStatus());
        extension.setAdminNote(request.getAdminNote());
        extension.setApprovedBy(admin.getId());
        extension.setApprovedAt(LocalDateTime.now());
        extension.setUpdatedAt(LocalDateTime.now());

        if(request.getExtensionStatus() == ExtensionStatus.APPROVED) {
            BookingEntity booking = bookingRepository.findById(extension.getBookingId()).orElseThrow(()-> new ResourceNotFoundException("booking not found"));

            ensureCarFreeForExtension(booking, extension.getRequestedDays());

            booking.setEndDate(booking.getEndDate().plusDays(extension.getRequestedDays()) );
            booking.setExtendedDays(booking.getExtendedDays() + extension.getRequestedDays());
            booking.setUpdatedAt(LocalDateTime.now());

            bookingRepository.save(booking);

        }
       BookingExtensionEntity updatedExtension =  bookingExtensionRepository.save(extension);
        return mapToResponse(updatedExtension);
    }

    public List<BookingExtensionResponse> getAllExtensions() {
        return bookingExtensionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BookingExtensionResponse> getExtensionsByBookingId(String bookingId) {
        return bookingExtensionRepository.findByBookingId(bookingId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }





    private BookingExtensionResponse mapToResponse(BookingExtensionEntity extension) {
        return BookingExtensionResponse.builder()
                .id(extension.getId())
                .extensionReference(extension.getExtensionReference())
                .bookingId(extension.getBookingId())
                .userId(extension.getUserId())
                .requestedDays(extension.getRequestedDays())
                .extensionCost(extension.getExtensionCost())
                .extensionStatus(extension.getExtensionStatus())
                .requestedAt(extension.getRequestedAt())
                .approvedBy(extension.getApprovedBy())
                .approvedAt(extension.getApprovedAt())
                .adminNote(extension.getAdminNote())
                .customerNote(extension.getCustomerNote())
                .createdAt(extension.getCreatedAt())
                .updatedAt(extension.getUpdatedAt())
                .build();

    }



}
