package com.wampert.wampert.repository;

import com.wampert.wampert.enums.InspectionStatus;
import com.wampert.wampert.enums.InspectionType;
import com.wampert.wampert.model.InspectionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InspectionRepository extends MongoRepository<InspectionEntity, String> {
    List<InspectionEntity> findByBookingId(String bookingId);
    Optional<InspectionEntity> findByBookingIdAndInspectionType(String bookingId, InspectionType inspectionType);


}
