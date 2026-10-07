package com.amar.Service.impl;

import com.amar.Service.PaymentService;
import com.amar.domain.PaymentOrderStatus;
import com.amar.domain.PaymentMethod;
import com.amar.modal.Order;
import com.amar.modal.PaymentOrder;
import com.amar.modal.PaymentStatus;
import com.amar.modal.User;
import com.amar.repository.OrderRepository;
import com.amar.repository.PaymentOrderRepository;
import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final OrderRepository orderRepository;

    @Value("${razorpay.key.id:}")
    private String apikey;

    @Value("${razorpay.key.secret:}")
    private String apiSecret;

    @Value("${stripe.secret.key:}")
    private String stripeSecret;


    // =========================
    // CREATE PAYMENT ORDER
    // =========================

    @Override
    public PaymentOrder createOrder(
            User user,
            Set<Order> orders) {

        long amount = 0;

        for (Order order : orders) {

            System.out.println(
                    "Order ID = " + order.getId()
            );

            System.out.println(
                    "Order Total Selling Price = "
                            + order.getTotalSellingPrice()
            );

            if (order.getTotalSellingPrice() != null) {
                amount += order.getTotalSellingPrice();
            }
        }

        System.out.println(
                "FINAL PAYMENT AMOUNT = " + amount
        );

        if (amount <= 0) {
            throw new RuntimeException(
                    "Order total amount is 0. Please check Order totalSellingPrice."
            );
        }

        PaymentOrder paymentOrder =
                new PaymentOrder();

        paymentOrder.setAmount(amount);
        paymentOrder.setUser(user);
        paymentOrder.setOrders(orders);

        paymentOrder.setStatus(
                PaymentOrderStatus.PENDING
        );

        return paymentOrderRepository.save(
                paymentOrder
        );
    }


    // =========================
    // FIND PAYMENT ORDER BY ID
    // =========================

    @Override
    public PaymentOrder getPaymentOrderById(
            Long orderID)
            throws Exception {

        return paymentOrderRepository
                .findById(orderID)
                .orElseThrow(() ->
                        new Exception(
                                "Payment order not found"
                        )
                );
    }


    // =========================
    // FIND PAYMENT ORDER BY PAYMENT ID
    // =========================

    @Override
    public PaymentOrder getPaymentOrderByPaymentId(
            String paymentId)
            throws Exception {

        PaymentOrder paymentOrder =
                paymentOrderRepository
                        .findByPaymentId(paymentId);

        if (paymentOrder == null) {

            throw new Exception(
                    "Payment order not found with provided payment id"
            );
        }

        return paymentOrder;
    }


    // =========================
    // PROCEED PAYMENT
    // =========================

    @Override
    public Boolean ProceedPaymentOrder(
            PaymentOrder paymentOrder,
            String paymentId,
            String paymentLinkId)
            throws RazorpayException {

        if (paymentOrder.getStatus()
                .equals(PaymentOrderStatus.PENDING)) {

            RazorpayClient razorpayClient =
                    new RazorpayClient(
                            apikey,
                            apiSecret
                    );

            Payment payment =
                    razorpayClient.payments.fetch(
                            paymentId
                    );

            String status =
                    payment.get("status");

            // =========================
            // PAYMENT SUCCESS
            // =========================

            if ("captured".equalsIgnoreCase(status)) {

                Set<Order> orders =
                        paymentOrder.getOrders();

                for (Order order : orders) {

                    order.setPaymentStatus(
                            PaymentStatus.COMPLETED
                    );

                    orderRepository.save(order);
                }

                paymentOrder.setStatus(
                        PaymentOrderStatus.SUCCESS
                );

                paymentOrder.setPaymentId(
                        paymentId
                );

                paymentOrder.setPaymentLinkId(
                        paymentLinkId
                );

                paymentOrderRepository.save(
                        paymentOrder
                );

                return true;
            }


            // =========================
            // PAYMENT FAILED
            // =========================

            if ("failed".equalsIgnoreCase(status)) {

                for (Order order :
                        paymentOrder.getOrders()) {

                    order.setPaymentStatus(
                            PaymentStatus.FAILED
                    );

                    orderRepository.save(order);
                }

                paymentOrder.setStatus(
                        PaymentOrderStatus.FAILED
                );

                paymentOrder.setPaymentId(
                        paymentId
                );

                paymentOrder.setPaymentLinkId(
                        paymentLinkId
                );

                paymentOrderRepository.save(
                        paymentOrder
                );

                return false;
            }


            // =========================
            // PAYMENT PENDING
            // =========================

            for (Order order :
                    paymentOrder.getOrders()) {

                order.setPaymentStatus(
                        PaymentStatus.PENDING
                );

                orderRepository.save(order);
            }

            paymentOrder.setStatus(
                    PaymentOrderStatus.PENDING
            );

            paymentOrder.setPaymentId(
                    paymentId
            );

            paymentOrder.setPaymentLinkId(
                    paymentLinkId
            );

            paymentOrderRepository.save(
                    paymentOrder
            );

            return false;
        }

        return paymentOrder.getStatus()
                == PaymentOrderStatus.SUCCESS;
    }


    // =========================
    // RAZORPAY PAYMENT LINK
    // =========================

    @Override
    public PaymentLink createRazorpayPaymentLink(
            User user,
            Long amount,
            Long orderId)
            throws RazorpayException {

        amount = amount * 100;

        try {

            RazorpayClient razorpayClient =
                    new RazorpayClient(
                            apikey,
                            apiSecret
                    );

            JSONObject paymentLinkRequest =
                    new JSONObject();

            paymentLinkRequest.put(
                    "amount",
                    amount
            );

            paymentLinkRequest.put(
                    "currency",
                    "INR"
            );


            JSONObject customer =
                    new JSONObject();

            customer.put(
                    "name",
                    user.getEmail()
            );

            paymentLinkRequest.put(
                    "customer",
                    customer
            );


            JSONObject notify =
                    new JSONObject();

            notify.put(
                    "email",
                    true
            );

            paymentLinkRequest.put(
                    "notify",
                    notify
            );


            paymentLinkRequest.put(
                    "callback_url",
                    "http://localhost:3000/payment-success/"
                            + orderId
            );

            paymentLinkRequest.put(
                    "callback_method",
                    "get"
            );


            return razorpayClient.paymentLink.create(
                    paymentLinkRequest
            );

        } catch (Exception e) {

            throw new RazorpayException(
                    e.getMessage()
            );
        }
    }


    // =========================
    // STRIPE PAYMENT LINK
    // =========================

    @Override
    public String createStripePaymentLink(
            User user,
            Long amount,
            Long orderId) {

        try {

            Stripe.apiKey =
                    stripeSecret;

            long amountInPaise =
                    amount * 100;


            SessionCreateParams params =
                    SessionCreateParams
                            .builder()

                            .addPaymentMethodType(
                                    SessionCreateParams
                                            .PaymentMethodType
                                            .CARD
                            )

                            .setMode(
                                    SessionCreateParams
                                            .Mode
                                            .PAYMENT
                            )

                            .setSuccessUrl(
                                    "http://localhost:3000/payment-success/"
                                            + orderId
                            )

                            .setCancelUrl(
                                    "http://localhost:3000/payment-failed/"
                                            + orderId
                            )

                            .setCustomerEmail(
                                    user.getEmail()
                            )

                            .addLineItem(
                                    SessionCreateParams
                                            .LineItem
                                            .builder()

                                            .setQuantity(1L)

                                            .setPriceData(
                                                    SessionCreateParams
                                                            .LineItem
                                                            .PriceData
                                                            .builder()

                                                            .setCurrency(
                                                                    "inr"
                                                            )

                                                            .setUnitAmount(
                                                                    amountInPaise
                                                            )

                                                            .setProductData(
                                                                    SessionCreateParams
                                                                            .LineItem
                                                                            .PriceData
                                                                            .ProductData
                                                                            .builder()
                                                                            .setName(
                                                                                    "Amar Bazaar Payment"
                                                                            )
                                                                            .build()
                                                            )

                                                            .build()
                                            )

                                            .build()
                            )

                            .build();


            Session session =
                    Session.create(params);

            return session.getUrl();

        } catch (StripeException e) {

            throw new RuntimeException(
                    "Stripe payment creation failed: "
                            + e.getMessage()
            );
        }
    }


    // =========================
    // CREATE RAZORPAY ORDER
    // =========================

    @Override
    public PaymentOrder createRazorpayOrder(
            PaymentOrder paymentOrder)
            throws RazorpayException {

        if (apikey == null
                || apikey.isBlank()
                || apiSecret == null
                || apiSecret.isBlank()) {

            throw new RazorpayException(
                    "Razorpay keys are not configured."
            );
        }

        Long amount =
                paymentOrder.getAmount();

        System.out.println(
                "================================="
        );

        System.out.println(
                "RAZORPAY ORDER"
        );

        System.out.println(
                "PaymentOrder ID = "
                        + paymentOrder.getId()
        );

        System.out.println(
                "PaymentOrder amount = "
                        + amount
        );

        if (amount == null || amount <= 0) {

            throw new RazorpayException(
                    "Invalid payment amount: "
                            + amount
            );
        }

        long amountInPaise =
                amount * 100;

        System.out.println(
                "Razorpay amount paise = "
                        + amountInPaise
        );

        System.out.println(
                "================================="
        );

        RazorpayClient razorpayClient =
                new RazorpayClient(
                        apikey,
                        apiSecret
                );

        JSONObject request =
                new JSONObject();

        request.put(
                "amount",
                amountInPaise
        );

        request.put(
                "currency",
                "INR"
        );

        request.put(
                "receipt",
                "PAY-" + paymentOrder.getId()
        );

        com.razorpay.Order razorpayOrder =
                razorpayClient.orders.create(
                        request
                );

        paymentOrder.setRazorpayOrderId(
                razorpayOrder.get("id")
        );

        paymentOrder.setPaymentMethod(
                PaymentMethod.RAZORPAY
        );

        return paymentOrderRepository.save(
                paymentOrder
        );
    }


    // =========================
    // VERIFY RAZORPAY PAYMENT
    // =========================

    @Override
    public boolean verifyRazorpayPayment(
            PaymentOrder paymentOrder,
            String razorpayPaymentId,
            String razorpayOrderId,
            String razorpaySignature)
            throws Exception {

        // -------------------------------------------------
        // CHECK PAYMENT ORDER
        // -------------------------------------------------

        if (paymentOrder == null) {

            throw new Exception(
                    "Payment order not found."
            );
        }


        // -------------------------------------------------
        // CHECK RAZORPAY ORDER ID
        // -------------------------------------------------

        if (paymentOrder.getRazorpayOrderId() == null
                || !paymentOrder
                .getRazorpayOrderId()
                .equals(razorpayOrderId)) {

            throw new Exception(
                    "Razorpay order id does not match our payment order."
            );
        }


        // -------------------------------------------------
        // ALREADY SUCCESS
        // -------------------------------------------------

        if (paymentOrder.getStatus()
                == PaymentOrderStatus.SUCCESS) {

            return true;
        }


        // -------------------------------------------------
        // ALREADY FAILED
        // -------------------------------------------------

        if (paymentOrder.getStatus()
                == PaymentOrderStatus.FAILED) {

            return false;
        }


        // -------------------------------------------------
        // CHECK REQUEST VALUES
        // -------------------------------------------------

        if (razorpayPaymentId == null
                || razorpayPaymentId.isBlank()
                || razorpaySignature == null
                || razorpaySignature.isBlank()) {

            throw new Exception(
                    "Missing Razorpay payment verification fields."
            );
        }


        // -------------------------------------------------
        // VERIFY RAZORPAY SIGNATURE
        // -------------------------------------------------

        JSONObject attributes =
                new JSONObject();

        attributes.put(
                "razorpay_order_id",
                razorpayOrderId
        );

        attributes.put(
                "razorpay_payment_id",
                razorpayPaymentId
        );

        attributes.put(
                "razorpay_signature",
                razorpaySignature
        );

        boolean valid =
                Utils.verifyPaymentSignature(
                        attributes,
                        apiSecret
                );

        if (!valid) {

            throw new Exception(
                    "Invalid Razorpay payment signature."
            );
        }


        // -------------------------------------------------
        // FETCH PAYMENT FROM RAZORPAY
        // -------------------------------------------------

        RazorpayClient razorpayClient =
                new RazorpayClient(
                        apikey,
                        apiSecret
                );

        Payment payment =
                razorpayClient.payments.fetch(
                        razorpayPaymentId
                );

        String status =
                payment.get("status");


        System.out.println(
                "RAZORPAY PAYMENT STATUS = "
                        + status
        );


        // -------------------------------------------------
        // PAYMENT CAPTURED = PAID
        // -------------------------------------------------

        if ("captured".equalsIgnoreCase(status)) {

            for (Order order :
                    paymentOrder.getOrders()) {

                order.setPaymentStatus(
                        PaymentStatus.COMPLETED
                );

                orderRepository.save(order);
            }

            paymentOrder.setStatus(
                    PaymentOrderStatus.SUCCESS
            );

            paymentOrder.setPaymentId(
                    razorpayPaymentId
            );

            paymentOrderRepository.save(
                    paymentOrder
            );

            System.out.println(
                    "RAZORPAY PAYMENT = PAID"
            );

            return true;
        }


        // -------------------------------------------------
        // PAYMENT FAILED = FAILED
        // -------------------------------------------------

        if ("failed".equalsIgnoreCase(status)) {

            for (Order order :
                    paymentOrder.getOrders()) {

                order.setPaymentStatus(
                        PaymentStatus.FAILED
                );

                orderRepository.save(order);
            }

            paymentOrder.setStatus(
                    PaymentOrderStatus.FAILED
            );

            paymentOrder.setPaymentId(
                    razorpayPaymentId
            );

            paymentOrderRepository.save(
                    paymentOrder
            );

            System.out.println(
                    "RAZORPAY PAYMENT = FAILED"
            );

            return false;
        }


        // -------------------------------------------------
        // ANY OTHER STATUS = PENDING
        // -------------------------------------------------

        for (Order order :
                paymentOrder.getOrders()) {

            order.setPaymentStatus(
                    PaymentStatus.PENDING
            );

            orderRepository.save(order);
        }

        paymentOrder.setStatus(
                PaymentOrderStatus.PENDING
        );

        paymentOrder.setPaymentId(
                razorpayPaymentId
        );

        paymentOrderRepository.save(
                paymentOrder
        );

        System.out.println(
                "RAZORPAY PAYMENT = PENDING"
        );

        return false;
    }
}