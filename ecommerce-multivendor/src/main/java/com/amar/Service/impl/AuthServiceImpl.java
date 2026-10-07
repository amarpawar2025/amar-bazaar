package com.amar.Service.impl;

import com.amar.Service.AuthService;
import com.amar.Service.EmailService;
import com.amar.config.JWTProvider;
import com.amar.domain.USER_ROLE;
import com.amar.modal.Cart;
import com.amar.modal.Seller;
import com.amar.modal.User;
import com.amar.modal.VerificationCode;
import com.amar.repository.CartRepository;
import com.amar.repository.SellerRepository;
import com.amar.repository.UserRepository;
import com.amar.repository.VerificationCodeRepository;
import com.amar.request.LoginRequest;
import com.amar.response.AuthResponse;
import com.amar.response.SignupRequest;
import com.amar.utils.OtpUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final SellerRepository sellerRepository;

    private final PasswordEncoder passwordEncoder;

    private final CartRepository cartRepository;

    private final JWTProvider jwtProvider;

    private final VerificationCodeRepository VerificationCodeRepository;

    private final EmailService emailService;

    private final CustomUserServiceImpl customUserService;


    // =====================================================
    // SEND LOGIN OTP
    // =====================================================

    @Override
    public void sentLoginOtp(
            String email,
            USER_ROLE role)
            throws Exception {

        email = email.trim().toLowerCase();

        String SIGNING_PREFIX = "signing_";


        // Remove signing_ prefix if present
        if (email.startsWith(SIGNING_PREFIX)) {

            email = email.substring(
                    SIGNING_PREFIX.length());
        }


        // =================================================
        // CUSTOMER LOGIN CHECK
        // =================================================

        if (role == USER_ROLE.ROLE_CUSTOMER) {

            User user =
                    userRepository.findByEmail(email);

            if (user == null) {

                throw new Exception(
                        "No account found with this email. Please create an account first."
                );
            }
        }


        // =================================================
        // SELLER LOGIN CHECK
        // =================================================

        if (role == USER_ROLE.ROLE_SELLER) {

            Seller seller =
                    sellerRepository.findByEmail(email);

            if (seller == null) {

                throw new Exception(
                        "No seller account found with this email."
                );
            }
        }


        // =================================================
        // DELETE OLD OTP
        // =================================================

        VerificationCode oldCode =
                VerificationCodeRepository
                        .findByEmail(email);

        if (oldCode != null) {

            VerificationCodeRepository
                    .delete(oldCode);
        }


        // =================================================
        // GENERATE OTP
        // =================================================

        String otp =
                OtpUtil.generateOtp();


        VerificationCode verificationCode =
                new VerificationCode();

        verificationCode.setOtp(otp);

        verificationCode.setEmail(email);


        VerificationCodeRepository.save(
                verificationCode);


        // =================================================
        // SEND EMAIL
        // =================================================

        String subject =
                "Amar Bazaar Login OTP";

        String text =
                "Your Amar Bazaar login OTP is - "
                        + otp;


        emailService.sendVerificationEmail(
                email,
                otp,
                subject,
                text);
    }


    // =====================================================
    // SEND REGISTER OTP
    // =====================================================

    @Override
    public void sentRegisterOtp(
            String email)
            throws Exception {

        email = email.trim().toLowerCase();


        // =================================================
        // CHECK EXISTING CUSTOMER
        // =================================================

        User existingUser =
                userRepository.findByEmail(email);


        if (existingUser != null) {

            throw new Exception(
                    "This email is already registered. Please log in to continue."
            );
        }


        // =================================================
        // DELETE OLD OTP
        // =================================================

        VerificationCode oldCode =
                VerificationCodeRepository
                        .findByEmail(email);

        if (oldCode != null) {

            VerificationCodeRepository
                    .delete(oldCode);
        }


        // =================================================
        // GENERATE OTP
        // =================================================

        String otp =
                OtpUtil.generateOtp();


        VerificationCode verificationCode =
                new VerificationCode();

        verificationCode.setOtp(otp);

        verificationCode.setEmail(email);


        VerificationCodeRepository.save(
                verificationCode);


        // =================================================
        // SEND EMAIL
        // =================================================

        String subject =
                "Amar Bazaar Registration OTP";

        String text =
                "Your Amar Bazaar registration OTP is - "
                        + otp;


        emailService.sendVerificationEmail(
                email,
                otp,
                subject,
                text);
    }


    // =====================================================
    // CREATE CUSTOMER
    // =====================================================

    @Override
    public String createUser(
            SignupRequest req)
            throws Exception {

        String email =
                req.getEmail()
                        .trim()
                        .toLowerCase();

        String otp =
                req.getOtp()
                        .trim();


        // =================================================
        // CHECK OTP
        // =================================================

        VerificationCode verificationCode =
                VerificationCodeRepository
                        .findByEmail(email);


        if (verificationCode == null ||
                !verificationCode
                        .getOtp()
                        .trim()
                        .equals(otp)) {

            throw new Exception(
                    "The OTP you entered is incorrect. Please try again."
            );
        }


        // =================================================
        // CHECK EXISTING USER
        // =================================================

        User existingUser =
                userRepository.findByEmail(email);


        if (existingUser != null) {

            throw new Exception(
                    "This email is already registered. Please log in to continue."
            );
        }


        // =================================================
        // CREATE USER
        // =================================================

        User user =
                new User();

        user.setEmail(email);

        user.setFullName(
                req.getFullName());

        user.setRole(
                USER_ROLE.ROLE_CUSTOMER);

        user.setMobile(
                "9405509570");

        user.setPassword(
                passwordEncoder.encode(otp));


        user =
                userRepository.save(user);


        // =================================================
        // CREATE CART
        // =================================================

        Cart cart =
                new Cart();

        cart.setUser(user);

        cartRepository.save(cart);


        // =================================================
        // AUTHENTICATION
        // =================================================

        List<GrantedAuthority> authorities =
                new ArrayList<>();


        authorities.add(
                new SimpleGrantedAuthority(
                        USER_ROLE.ROLE_CUSTOMER
                                .toString()
                ));


        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        authorities);


        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        authentication);


        // =================================================
        // GENERATE JWT
        // =================================================

        return jwtProvider.generateJWT(
                authentication);
    }


    // =====================================================
    // LOGIN
    // =====================================================

    @Override
    public AuthResponse signing(
            LoginRequest req)
            throws Exception {

        String username =
                req.getEmail()
                        .trim()
                        .toLowerCase();

        String otp =
                req.getOtp()
                        .trim();


        Authentication authentication =
                authenticate(
                        username,
                        otp);


        String token =
                jwtProvider.generateJWT(
                        authentication);


        AuthResponse authResponse =
                new AuthResponse();


        authResponse.setJwt(token);


        authResponse.setMessage(
                "Login successful. Welcome back to Amar Bazaar!"
        );


        Collection<? extends GrantedAuthority>
                authorities =
                authentication
                        .getAuthorities();


        String roleName =
                authorities.isEmpty()
                        ? null
                        : authorities
                        .iterator()
                        .next()
                        .getAuthority();


        authResponse.setRole(
                USER_ROLE.valueOf(
                        roleName));


        return authResponse;
    }


    // =====================================================
    // AUTHENTICATE
    // =====================================================

    private Authentication authenticate(
            String username,
            String otp)
            throws Exception {

        String loginEmail =
                username.trim();


        // =================================================
        // SELLER LOGIN
        // =================================================

        boolean sellerLogin =
                loginEmail.startsWith(
                        "seller_");


        // =================================================
        // GET ORIGINAL EMAIL
        // =================================================

        String originalEmail =
                loginEmail;


        if (sellerLogin) {

            originalEmail =
                    loginEmail.substring(
                            "seller_".length());

        } else if (
                loginEmail.startsWith(
                        "signing_")) {

            originalEmail =
                    loginEmail.substring(
                            "signing_".length());
        }


        // =================================================
        // USERNAME FOR USER DETAILS
        // =================================================

        String lookupUsername;


        if (sellerLogin) {

            lookupUsername =
                    "seller_" +
                            originalEmail;

        } else {

            lookupUsername =
                    originalEmail;
        }


        // =================================================
        // LOAD USER
        // =================================================

        UserDetails userDetails =
                customUserService
                        .loadUserByUsername(
                                lookupUsername);


        if (userDetails == null) {

            throw new BadCredentialsException(
                    "We could not find an account with this email."
            );
        }


        // =================================================
        // FIND OTP
        // =================================================

        VerificationCode verificationCode =
                VerificationCodeRepository
                        .findByEmail(
                                originalEmail);


        if (verificationCode == null) {

            throw new Exception(
                    "Your OTP is invalid or expired. Please request a new OTP."
            );
        }


        // =================================================
        // COMPARE OTP
        // =================================================

        String savedOtp =
                verificationCode
                        .getOtp()
                        .trim();

        String enteredOtp =
                otp.trim();


        if (!savedOtp.equals(
                enteredOtp)) {

            throw new Exception(
                    "The OTP you entered is incorrect. Please try again."
            );
        }


        // =================================================
        // LOGIN SUCCESS
        // =================================================

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
    }
}