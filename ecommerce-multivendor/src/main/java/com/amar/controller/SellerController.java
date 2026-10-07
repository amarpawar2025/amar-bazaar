package com.amar.controller;

import com.amar.Service.AuthService;
import com.amar.Service.EmailService;
import com.amar.Service.SellerReportService;
import com.amar.Service.SellerService;

import com.amar.domain.AccountStatus;
import com.amar.domain.USER_ROLE;

import com.amar.exceptions.SellerException;

import com.amar.modal.Seller;
import com.amar.modal.SellerReport;
import com.amar.modal.VerificationCode;

import com.amar.repository.VerificationCodeRepository;

import com.amar.request.LoginRequest;

import com.amar.response.ApiResponse;
import com.amar.response.AuthResponse;

import com.amar.utils.OtpUtil;

import jakarta.mail.MessagingException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/sellers")
public class SellerController {

    private final SellerService sellerService;

    private final SellerReportService sellerReportService;

    private final VerificationCodeRepository verificationCodeRepository;

    private final AuthService authService;

    private final EmailService emailService;


    // =========================================================
    // SEND LOGIN OTP
    // =========================================================

    @PostMapping("/sent/login-otp")
    public ResponseEntity<ApiResponse> sentOtpHandler(
            @RequestBody VerificationCode req)
            throws Exception {

        authService.sentLoginOtp(
                req.getEmail(),
                USER_ROLE.ROLE_SELLER
        );

        ApiResponse res = new ApiResponse();

        res.setMessage("otp sent Successfully");

        return ResponseEntity.ok(res);
    }


    // =========================================================
    // SELLER LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginSeller(
            @RequestBody LoginRequest req)
            throws Exception {

        String email = req.getEmail();

        req.setEmail("seller_" + email);

        AuthResponse authResponse =
                authService.signing(req);

        return ResponseEntity.ok(authResponse);
    }


    // =========================================================
    // VERIFY SELLER EMAIL
    // =========================================================

    @PatchMapping("/verify/{otp}")
    public ResponseEntity<Seller> verifySellerEmail(
            @PathVariable String otp)
            throws Exception {

        VerificationCode verificationCode =
                verificationCodeRepository.findByOtp(otp);

        if (
                verificationCode == null ||
                        !verificationCode.getOtp().equals(otp)
        ) {
            throw new Exception("wrong otp.....");
        }

        Seller seller =
                sellerService.verifyEmail(
                        verificationCode.getEmail(),
                        otp
                );

        return new ResponseEntity<>(
                seller,
                HttpStatus.OK
        );
    }


    // =========================================================
    // CREATE SELLER
    // =========================================================

    @PostMapping
    public ResponseEntity<Seller> createSeller(
            @RequestBody Seller seller)
            throws Exception, MessagingException {

        Seller savedSeller =
                sellerService.CreateSeller(seller);

        String otp =
                OtpUtil.generateOtp();

        VerificationCode verificationCode =
                new VerificationCode();

        verificationCode.setOtp(otp);

        verificationCode.setEmail(
                seller.getEmail()
        );

        verificationCodeRepository.save(
                verificationCode
        );

        String subject =
                "amar bazaar Email Verification Code";

        String text =
                "Welcome to amar Bazaar, verify your account Using this link";

        String frontend_url =
                "http://localhost:3000/verify-seller/";

        emailService.sendVerificationEmail(
                seller.getEmail(),
                verificationCode.getOtp(),
                subject,
                text + frontend_url
        );

        return new ResponseEntity<>(
                savedSeller,
                HttpStatus.OK
        );
    }


    // =========================================================
    // GET SELLER BY ID
    // =========================================================

    @GetMapping("/id/{id}")
    public ResponseEntity<Seller> getSellerById(
            @PathVariable Long id)
            throws SellerException {

        Seller seller =
                sellerService.getSellerById(id);

        return new ResponseEntity<>(
                seller,
                HttpStatus.OK
        );
    }


    // =========================================================
    // GET SELLER PROFILE
    // =========================================================

    @GetMapping("/profile")
    public ResponseEntity<Seller> getSellerByJwt(
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        Seller seller =
                sellerService.getSellerProfile(jwt);

        return new ResponseEntity<>(
                seller,
                HttpStatus.OK
        );
    }


    // =========================================================
    // GET ALL SELLERS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Seller>> getAllSellers(
            @RequestParam(required = false)
            AccountStatus status) {

        List<Seller> sellers =
                sellerService.getAllSellers(status);

        return ResponseEntity.ok(sellers);
    }


    // =========================================================
    // UPDATE SELLER
    // =========================================================

    @PatchMapping
    public ResponseEntity<Seller> updateSeller(
            @RequestHeader("Authorization") String jwt,
            @RequestBody Seller seller)
            throws Exception {

        Seller profile =
                sellerService.getSellerProfile(jwt);

        Seller updateSeller =
                sellerService.updateSeller(
                        profile.getId(),
                        seller
                );

        return ResponseEntity.ok(updateSeller);
    }


    // =========================================================
    // DELETE SELLER
    // =========================================================

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteSeller(
            @PathVariable Long id)
            throws Exception {

        sellerService.DeleteSeller(id);

        return ResponseEntity
                .noContent()
                .build();
    }


    // =========================================================
    // SELLER REPORT
    // =========================================================

    @GetMapping("/report")
    public ResponseEntity<SellerReport> getSellerReport(
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        Seller seller =
                sellerService.getSellerProfile(jwt);

        SellerReport report =
                sellerReportService.getSellerReportById(
                        seller
                );

        return new ResponseEntity<>(
                report,
                HttpStatus.OK
        );
    }
}