package com.wampart.wampart.repositoty;

import com.wampart.wampart.enums.PaymentStatus;
import com.wampart.wampart.model.PaymentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends MongoRepository<PaymentEntity, String> {
    Optional<PaymentEntity> findByBookingId(String bookingId);
    List<PaymentEntity> findByUserId(String userId);
    List<PaymentEntity> findByPaymentStatus(PaymentStatus paymentStatus);
    Boolean existsByBookingId(String bookingId);
    List<PaymentEntity> findByUserIdAndPaymentStatus(String userId, PaymentStatus paymentStatus);
}
