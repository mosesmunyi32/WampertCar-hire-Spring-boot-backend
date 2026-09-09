package com.wampert.wampert.repository;


import com.wampert.wampert.enums.ExtensionStatus;
import com.wampert.wampert.model.BookingExtensionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingExtensionRepository extends MongoRepository<BookingExtensionEntity, String> {

    Optional<BookingExtensionEntity> findByExtensionReference(String extensionReference);
    List<BookingExtensionEntity> findByBookingId(String bookingId);
    List<BookingExtensionEntity> findByUserId(String userId);
    List<BookingExtensionEntity> findByExtensionStatus(ExtensionStatus bookingStatus);
    List<BookingExtensionEntity> findByBookingIdAndExtensionStatus(String bookingId, ExtensionStatus extensionStatus);




}
