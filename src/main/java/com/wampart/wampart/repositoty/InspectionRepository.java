package com.wampart.wampart.repositoty;

import com.wampart.wampart.enums.InspectionStatus;
import com.wampart.wampart.enums.InspectionType;
import com.wampart.wampart.model.InspectionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InspectionRepository extends MongoRepository<InspectionEntity, String> {
    List<InspectionEntity> findByBookingId(String bookingId);
    Optional<InspectionEntity> findByBookingIdAndInspectionType(String bookingId, InspectionType inspectionType);


}
