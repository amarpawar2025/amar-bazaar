package com.amar.Service;

import com.amar.modal.Order;
import com.amar.modal.PaymentOrder;
import com.amar.modal.User;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayException;

import java.util.Set;

public interface PaymentService {

    PaymentOrder createOrder(User user, Set<Order> orders);

    PaymentOrder getPaymentOrderById(Long orderID) throws Exception;

    PaymentOrder getPaymentOrderByPaymentId(String paymentId) throws Exception;

    Boolean ProceedPaymentOrder(PaymentOrder paymentOrder, String paymentId, String paymentLinkId)
            throws RazorpayException;

    PaymentLink createRazorpayPaymentLink(User user, Long amount, Long orderId)
            throws RazorpayException;

    String createStripePaymentLink(User user, Long amount, Long orderId);

    PaymentOrder createRazorpayOrder(PaymentOrder paymentOrder) throws RazorpayException;

    boolean verifyRazorpayPayment(PaymentOrder paymentOrder,
                                  String razorpayPaymentId,
                                  String razorpayOrderId,
                                  String razorpaySignature) throws Exception;
}
