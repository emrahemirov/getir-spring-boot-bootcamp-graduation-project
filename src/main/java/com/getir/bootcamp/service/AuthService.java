package com.getir.bootcamp.service;

import com.getir.bootcamp.dto.request.SignInRequest;
import com.getir.bootcamp.dto.request.SignUpRequest;
import com.getir.bootcamp.dto.response.JwtAuthResponse;
import com.getir.bootcamp.entity.Role;
import com.getir.bootcamp.entity.User;
import com.getir.bootcamp.mapper.AuthMapper;
import com.getir.bootcamp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public JwtAuthResponse signUp(SignUpRequest signUpRequest) {
        User user = authMapper.signUpRequestToUser(signUpRequest);
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("username_already_exists");
        }
        user.setRole(Role.ROLE_PATRON);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return getJwtAuthResponse(signUpRequest.username(), signUpRequest.password());
    }

    public JwtAuthResponse signIn(SignInRequest signInRequest) {
        return getJwtAuthResponse(signInRequest.username(), signInRequest.password());
    }

    public JwtAuthResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username).orElseThrow();

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("invalid_refresh_token");
        }
        String accessToken = jwtService.generateAccessToken(user);
        return new JwtAuthResponse(accessToken, refreshToken);
    }

    private JwtAuthResponse getJwtAuthResponse(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );

        User user = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new JwtAuthResponse(accessToken, refreshToken);
    }
}
