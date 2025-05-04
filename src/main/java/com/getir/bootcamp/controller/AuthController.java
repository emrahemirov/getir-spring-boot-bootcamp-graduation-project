package com.getir.bootcamp.controller;

import com.getir.bootcamp.config.TokenProperties;
import com.getir.bootcamp.dto.CommonResponse;
import com.getir.bootcamp.dto.request.SignInRequest;
import com.getir.bootcamp.dto.request.SignUpRequest;
import com.getir.bootcamp.dto.response.JwtAuthResponse;
import com.getir.bootcamp.exception.ExceptionMessages;
import com.getir.bootcamp.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final TokenProperties tokenProperties;
    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<CommonResponse<JwtAuthResponse>> signUp(@RequestBody SignUpRequest signUpRequest, HttpServletResponse response) {
        JwtAuthResponse authResponse = authService.signUp(signUpRequest);
        return getJwtAuthResponseResponseEntity(response, authResponse);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<CommonResponse<JwtAuthResponse>> signIn(@RequestBody SignInRequest signInRequest, HttpServletResponse response) {
        JwtAuthResponse authResponse = authService.signIn(signInRequest);
        return getJwtAuthResponseResponseEntity(response, authResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<CommonResponse<JwtAuthResponse>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> refreshTokenOpt = Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> tokenProperties.getRefreshToken().getCookieName().equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                );

        if (refreshTokenOpt.isEmpty()) {
            CommonResponse<JwtAuthResponse> errorResponse = CommonResponse.error(ExceptionMessages.REFRESH_TOKEN_IS_MISSING);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        String refreshToken = refreshTokenOpt.get();
        JwtAuthResponse authResponse = authService.refreshToken(refreshToken);
        return getJwtAuthResponseResponseEntity(response, authResponse);
    }

    private ResponseEntity<CommonResponse<JwtAuthResponse>> getJwtAuthResponseResponseEntity(HttpServletResponse response, JwtAuthResponse authResponse) {
        Cookie refreshTokenCookie = new Cookie(tokenProperties.getRefreshToken().getCookieName(), authResponse.refreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(Math.toIntExact(tokenProperties.getRefreshToken().getMaxAge()));

        response.addCookie(refreshTokenCookie);

        CommonResponse<JwtAuthResponse> commonResponse = CommonResponse.ok(authResponse);
        return ResponseEntity.ok(commonResponse);
    }
}
