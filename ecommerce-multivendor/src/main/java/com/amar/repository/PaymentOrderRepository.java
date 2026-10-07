package com.amar.repository;

import com.amar.modal.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOrderRepository
        extends JpaRepository<PaymentOrder, Long> {

    PaymentOrder findByPaymentId(String paymentId);

    PaymentOrder findByPaymentLinkId(String paymentLinkId);
}