package com.wampart.wampart.repositoty;

import com.wampart.wampart.enums.BookingStatus;
import com.wampart.wampart.model.BookingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends MongoRepository<BookingEntity, String> {
    Optional<BookingEntity> findByBookingReference(String bookingReference);
    Boolean existsByBookingReference(String bookingReference);
    List<BookingEntity> findByUserId(String userId);
    List<BookingEntity> findByBookingStatus(BookingStatus bookingStatus);
    List<BookingEntity> findByCarId(String carId);


    //Check for Overlapping Bookings
    @Query("""
            {
            'carId': ?0,
            'bookingStatus' : { $in: ['PENDING', 'CONFIRMED'] },
            'startDate': {$lt: ?2},
             'endDate':  {$gt: ?1}
             }
           """)
    List<BookingEntity> findOverlappingBookings(String carId, LocalDateTime startDate, LocalDateTime endDate);



}
