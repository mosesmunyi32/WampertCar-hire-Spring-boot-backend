package com.wampert.wampert.repository;

import com.wampert.wampert.enums.RefundStatus;
import com.wampert.wampert.model.RefundEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends MongoRepository<RefundEntity, String> {
    Optional<RefundEntity> findByRefundReference(String refundReference);
    Boolean existsByRefundReference(String refundReference);
    Optional<RefundEntity> findByBookingId(String bookingId);
    Optional<RefundEntity> findByUserId(String userId);
    Optional<RefundEntity> findByPaymentId(String paymentId);
    List<RefundEntity> findByRefundStatus(RefundStatus refundStatus);
    List<RefundEntity> findByUserIdAndRefundStatus(String userId, RefundStatus refundStatus);
}


