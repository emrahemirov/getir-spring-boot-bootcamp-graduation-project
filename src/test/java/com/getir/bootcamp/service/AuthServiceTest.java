package com.getir.bootcamp.service;

import com.getir.bootcamp.dto.request.SignInRequest;
import com.getir.bootcamp.dto.request.SignUpRequest;
import com.getir.bootcamp.dto.response.JwtAuthResponse;
import com.getir.bootcamp.entity.Role;
import com.getir.bootcamp.entity.User;
import com.getir.bootcamp.exception.BadRequestException;
import com.getir.bootcamp.exception.ExceptionMessages;
import com.getir.bootcamp.mapper.UserMapper;
import com.getir.bootcamp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;


    private SignUpRequest signUpRequest;
    private SignInRequest signInRequest;
    private User user;

    @BeforeEach
    void setUp() {

        signUpRequest = new SignUpRequest("John", "Doe", "john_doe", "P@ssw0rd123");
        signInRequest = new SignInRequest("john_doe", "P@ssw0rd123");

        user = new User();
        user.setUsername("john_doe");
        user.setPassword("P@ssw0rd123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.ROLE_PATRON);
        user.setCanBorrow(true);
    }


    @Test
    void signUp_ShouldCreateUserAndReturnTokens() {
        when(userMapper.signUpRequestToUserEntity(signUpRequest)).thenReturn(user);
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(passwordEncoder.encode("P@ssw0rd123")).thenReturn("hashed_pass");
        when(userRepository.save(user)).thenReturn(user);

        // simulate inner auth flow
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");

        JwtAuthResponse response = authService.signUp(signUpRequest);

        assertNotNull(response);
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        verify(userRepository).save(user);
    }

    @Test
    void signUp_ShouldThrowException_WhenUsernameExists() {
        when(userMapper.signUpRequestToUserEntity(signUpRequest)).thenReturn(user);
        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.signUp(signUpRequest));
        assertEquals(ExceptionMessages.USERNAME_ALREADY_EXISTS, ex.getMessage());
    }

    @Test
    void signIn_ShouldAuthenticateAndReturnTokens() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");

        JwtAuthResponse response = authService.signIn(signInRequest);

        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
    }

    @Test
    void signIn_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authService.signIn(signInRequest));
    }

    @Test
    void refreshToken_ShouldReturnNewAccessToken_WhenTokenIsValid() {
        when(jwtService.extractUsername("valid-refresh")).thenReturn("john_doe");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid("valid-refresh", user)).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("new-access-token");

        JwtAuthResponse result = authService.refreshToken("valid-refresh");

        assertEquals("new-access-token", result.accessToken());
        assertEquals("valid-refresh", result.refreshToken());
    }

    @Test
    void refreshToken_ShouldThrow_WhenTokenIsInvalid() {
        when(jwtService.extractUsername("invalid-refresh")).thenReturn("john_doe");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid("invalid-refresh", user)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.refreshToken("invalid-refresh"));
    }
}
