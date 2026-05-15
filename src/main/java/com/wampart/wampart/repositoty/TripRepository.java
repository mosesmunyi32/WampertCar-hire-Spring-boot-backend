package com.wampart.wampart.repositoty;

import com.wampart.wampart.enums.TripStatus;
import com.wampart.wampart.model.TripEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends MongoRepository<TripEntity, String> {
    Optional<TripEntity> findByBookingId(String BookingId);
    List<TripEntity> findByCarId(String carId);
    List<TripEntity> findByUserId(String userId);
    List<TripEntity> findByTripStatus(TripStatus tripStatus);
    List<TripEntity> findByTripStatusAndCarId(TripStatus tripStatus, String carId);
    List<TripEntity> findByTripStatusAndUserId(TripStatus tripStatus, String userId);

}
