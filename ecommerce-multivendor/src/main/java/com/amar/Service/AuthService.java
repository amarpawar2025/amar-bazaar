package com.amar.Service;

import com.amar.domain.USER_ROLE;
import com.amar.request.LoginRequest;
import com.amar.response.AuthResponse;
import com.amar.response.SignupRequest;

public interface AuthService {

    void sentLoginOtp(
            String email,
            USER_ROLE role) throws Exception;

    void sentRegisterOtp(
            String email) throws Exception;

    String createUser(
            SignupRequest req) throws Exception;

    AuthResponse signing(
            LoginRequest req) throws Exception;
}