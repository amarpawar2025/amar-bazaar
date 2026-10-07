package com.amar.controller;

import com.amar.Service.CartService;
import com.amar.Service.PaymentService;
import com.amar.Service.SellerReportService;
import com.amar.Service.SellerService;
import com.amar.Service.TransactionService;
import com.amar.domain.PaymentOrderStatus;
import com.amar.modal.Order;
import com.amar.modal.PaymentOrder;
import com.amar.modal.Seller;
import com.amar.modal.SellerReport;
import com.amar.response.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    private final SellerService sellerService;

    private final SellerReportService sellerReportService;

    private final TransactionService transactionService;

    /*
     * Cart service is used to clear the user's cart
     * after successful Razorpay payment.
     */
    private final CartService cartService;


    // =========================================================
    // RAZORPAY PAYMENT VERIFICATION
    // =========================================================

    @PostMapping("/razorpay/verify")
    public ResponseEntity<?> verifyRazorpayPayment(
            @RequestBody Map<String, String> request)
            throws Exception {

        System.out.println(
                "========== RAZORPAY PAYMENT VERIFY =========="
        );


        // -----------------------------------------------------
        // GET RAZORPAY RESPONSE
        // -----------------------------------------------------

        String razorpayPaymentId =
                request.get("razorpay_payment_id");

        String razorpayOrderId =
                request.get("razorpay_order_id");

        String razorpaySignature =
                request.get("razorpay_signature");

        String paymentOrderIdString =
                request.get("paymentOrderId");


        System.out.println(
                "Razorpay Payment ID: "
                        + razorpayPaymentId
        );

        System.out.println(
                "Razorpay Order ID: "
                        + razorpayOrderId
        );

        System.out.println(
                "Payment Order ID: "
                        + paymentOrderIdString
        );


        // -----------------------------------------------------
        // VALIDATE REQUEST
        // -----------------------------------------------------

        if (razorpayPaymentId == null
                || razorpayOrderId == null
                || razorpaySignature == null
                || paymentOrderIdString == null) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "success", false,
                            "status", "FAILED",
                            "message",
                            "Missing Razorpay verification fields"
                    ));
        }


        // -----------------------------------------------------
        // CONVERT PAYMENT ORDER ID
        // -----------------------------------------------------

        Long paymentOrderId;

        try {

            paymentOrderId =
                    Long.valueOf(paymentOrderIdString);

        } catch (NumberFormatException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "success", false,
                            "status", "FAILED",
                            "message",
                            "Invalid paymentOrderId"
                    ));
        }


        // -----------------------------------------------------
        // GET PAYMENT ORDER
        // -----------------------------------------------------

        PaymentOrder paymentOrder =
                paymentService.getPaymentOrderById(
                        paymentOrderId
                );

        if (paymentOrder == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "status", "FAILED",
                            "message",
                            "Payment order not found"
                    ));
        }


        // -----------------------------------------------------
        // CHECK IF PAYMENT WAS ALREADY SUCCESSFUL
        // -----------------------------------------------------

        boolean alreadySuccessful =
                paymentOrder.getStatus()
                        == PaymentOrderStatus.SUCCESS;


        // -----------------------------------------------------
        // VERIFY RAZORPAY PAYMENT
        // -----------------------------------------------------

        boolean success =
                paymentService.verifyRazorpayPayment(
                        paymentOrder,
                        razorpayPaymentId,
                        razorpayOrderId,
                        razorpaySignature
                );


        // -----------------------------------------------------
        // REFRESH PAYMENT STATUS
        // -----------------------------------------------------

        PaymentOrderStatus currentStatus =
                paymentOrder.getStatus();


        // =====================================================
        // PAYMENT FAILED
        // =====================================================

        if (!success
                && currentStatus
                == PaymentOrderStatus.FAILED) {

            System.out.println(
                    "RAZORPAY PAYMENT FAILED"
            );

            return ResponseEntity
                    .ok()
                    .body(Map.of(
                            "success", false,
                            "status", "FAILED",
                            "message",
                            "Payment failed. Please try again."
                    ));
        }


        // =====================================================
        // PAYMENT PENDING
        // =====================================================

        if (!success
                && currentStatus
                == PaymentOrderStatus.PENDING) {

            System.out.println(
                    "RAZORPAY PAYMENT PENDING"
            );

            return ResponseEntity
                    .ok()
                    .body(Map.of(
                            "success", false,
                            "status", "PENDING",
                            "message",
                            "Payment is still pending. Please check your order status."
                    ));
        }


        // =====================================================
        // PAYMENT SUCCESS
        // =====================================================

        if (success
                && currentStatus
                == PaymentOrderStatus.SUCCESS) {


            // -------------------------------------------------
            // FIRST SUCCESSFUL PAYMENT
            // -------------------------------------------------

            if (!alreadySuccessful) {

                for (Order order :
                        paymentOrder.getOrders()) {


                    // -----------------------------------------
                    // CREATE TRANSACTION
                    // -----------------------------------------

                    transactionService.createTransaction(
                            order
                    );


                    // -----------------------------------------
                    // GET SELLER
                    // -----------------------------------------

                    Seller seller =
                            sellerService.getSellerById(
                                    order.getSellerId()
                            );


                    // -----------------------------------------
                    // GET SELLER REPORT
                    // -----------------------------------------

                    SellerReport report =
                            sellerReportService.getSellerReport(
                                    seller
                            );


                    // -----------------------------------------
                    // UPDATE TOTAL ORDERS
                    // -----------------------------------------

                    report.setTotalOrders(
                            report.getTotalOrders() + 1
                    );


                    // -----------------------------------------
                    // UPDATE TOTAL EARNINGS
                    // -----------------------------------------

                    report.setTotalEarnings(
                            report.getTotalEarnings()
                                    + order.getTotalSellingPrice()
                    );


                    // -----------------------------------------
                    // UPDATE TOTAL SALES
                    // -----------------------------------------

                    report.setTotalSales(
                            report.getTotalSales()
                                    + order.getOrderItems().size()
                    );


                    // -----------------------------------------
                    // SAVE SELLER REPORT
                    // -----------------------------------------

                    sellerReportService.updateSellerReport(
                            report
                    );
                }


                // -------------------------------------------------
                // CLEAR USER CART
                // -------------------------------------------------

                /*
                 * Payment is successfully completed.
                 *
                 * Therefore remove all items from
                 * the user's cart.
                 */
                if (paymentOrder.getUser() != null) {

                    cartService.clearCart(
                            paymentOrder.getUser()
                    );

                    System.out.println(
                            "USER CART CLEARED AFTER SUCCESSFUL PAYMENT"
                    );
                }

            } else {

                System.out.println(
                        "PAYMENT ALREADY PROCESSED - "
                                + "SKIPPING TRANSACTION, "
                                + "SELLER REPORT AND CART CLEAR"
                );
            }


            System.out.println(
                    "RAZORPAY PAYMENT VERIFIED SUCCESSFULLY"
            );

            System.out.println(
                    "============================================"
            );


            return ResponseEntity
                    .ok()
                    .body(Map.of(
                            "success", true,
                            "status", "PAID",
                            "message",
                            "Payment successful. Your order has been placed."
                    ));
        }


        // =====================================================
        // FALLBACK
        // =====================================================

        return ResponseEntity
                .ok()
                .body(Map.of(
                        "success", false,
                        "status", "PENDING",
                        "message",
                        "Payment status is being processed."
                ));
    }


    // =========================================================
    // LEGACY PAYMENT LINK CALLBACK
    // =========================================================

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse> paymentSuccessHandler(
            @PathVariable String paymentId,
            @RequestParam String paymentLinkId,
            @RequestHeader("Authorization") String jwt)
            throws Exception {


        PaymentOrder paymentOrder =
                paymentService.getPaymentOrderByPaymentId(
                        paymentId
                );


        boolean paymentSuccess =
                paymentService.ProceedPaymentOrder(
                        paymentOrder,
                        paymentId,
                        paymentLinkId
                );


        ApiResponse res =
                new ApiResponse();


        res.setMessage(
                paymentSuccess
                        ? "payment Successful"
                        : "payment Failed"
        );


        return new ResponseEntity<>(
                res,
                paymentSuccess
                        ? HttpStatus.CREATED
                        : HttpStatus.BAD_REQUEST
        );
    }
}