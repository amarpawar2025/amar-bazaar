package com.amar.controller;

import com.amar.Service.AuthService;
import com.amar.domain.USER_ROLE;
import com.amar.request.LoginOtpRequest;
import com.amar.request.LoginRequest;
import com.amar.response.ApiResponse;
import com.amar.response.AuthResponse;
import com.amar.response.SignupRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // =====================================================
    // SIGNUP
    // =====================================================

    @PostMapping("/signup")
    public ResponseEntity<?> createUserHandler(
            @RequestBody SignupRequest req) {

        try {

            String jwt =
                    authService.createUser(req);

            AuthResponse res =
                    new AuthResponse();

            res.setJwt(jwt);

            res.setMessage(
                    "Your account has been created successfully. Welcome to Amar Bazaar!"
            );

            res.setRole(
                    USER_ROLE.ROLE_CUSTOMER
            );

            return ResponseEntity.ok(res);

        } catch (Exception e) {

            ApiResponse res =
                    new ApiResponse();

            res.setMessage(
                    e.getMessage() != null
                            ? e.getMessage()
                            : "Registration failed. Please try again."
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(res);
        }
    }

    // =====================================================
    // LOGIN / SIGNUP OTP
    // =====================================================

    @PostMapping("/sent/login-signup-otp")
    public ResponseEntity<?> sentOtpHandler(
            @RequestBody LoginOtpRequest req) {

        try {

            authService.sentLoginOtp(
                    req.getEmail(),
                    req.getRole()
            );

            ApiResponse res =
                    new ApiResponse();

            res.setMessage(
                    "OTP sent successfully. Please check your email."
            );

            return ResponseEntity.ok(res);

        } catch (Exception e) {

            ApiResponse res =
                    new ApiResponse();

            res.setMessage(
                    e.getMessage() != null
                            ? e.getMessage()
                            : "Unable to send OTP. Please try again."
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(res);
        }
    }

    // =====================================================
    // REGISTER OTP
    // =====================================================

    @PostMapping("/sent/register-otp")
    public ResponseEntity<?> sentRegisterOtpHandler(
            @RequestBody LoginOtpRequest req) {

        try {

            authService.sentRegisterOtp(
                    req.getEmail()
            );

            ApiResponse res =
                    new ApiResponse();

            res.setMessage(
                    "Registration OTP sent successfully. Please check your email."
            );

            return ResponseEntity.ok(res);

        } catch (Exception e) {

            ApiResponse res =
                    new ApiResponse();

            res.setMessage(
                    e.getMessage() != null
                            ? e.getMessage()
                            : "Unable to send registration OTP. Please try again."
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(res);
        }
    }

    // =====================================================
    // CUSTOMER LOGIN
    // =====================================================

    @PostMapping("/signing")
    public ResponseEntity<?> loginHandler(
            @RequestBody LoginRequest req) {

        try {

            AuthResponse authResponse =
                    authService.signing(req);

            return ResponseEntity.ok(
                    authResponse
            );

        } catch (Exception e) {

            ApiResponse res =
                    new ApiResponse();

            res.setMessage(
                    e.getMessage() != null
                            ? e.getMessage()
                            : "Login failed. Please try again."
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(res);
        }
    }
}