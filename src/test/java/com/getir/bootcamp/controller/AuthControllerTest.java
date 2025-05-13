package com.getir.bootcamp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getir.bootcamp.config.TokenProperties;
import com.getir.bootcamp.dto.request.SignInRequest;
import com.getir.bootcamp.dto.request.SignUpRequest;
import com.getir.bootcamp.entity.User;
import com.getir.bootcamp.exception.ExceptionMessages;
import com.getir.bootcamp.repository.UserRepository;
import com.getir.bootcamp.service.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private TokenProperties tokenProperties;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void signUp_ShouldCreateUserAndReturnTokens() throws Exception {
        SignUpRequest request = new SignUpRequest(
                "John", "Doe", "john_doe", "P@ssword123"
        );

        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());

        assertThat(userRepository.existsByUsername("john_doe")).isTrue();
    }

    @Test
    void signIn_ShouldAuthenticateUserAndReturnTokens() throws Exception {
        // Pre-save user
        User user = new User();
        user.setUsername("john_doe");
        user.setPassword(passwordEncoder.encode("P@ssword123"));
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setCanBorrow(true);
        user.setRole(com.getir.bootcamp.entity.Role.ROLE_PATRON);
        userRepository.save(user);

        SignInRequest request = new SignInRequest("john_doe", "P@ssword123");

        mockMvc.perform(post("/api/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    void refreshToken_ShouldReturnNewAccessToken_WhenCookieIsPresent() throws Exception {
        // Pre-save user
        User user = new User();
        user.setUsername("john_doe");
        user.setPassword(passwordEncoder.encode("secret"));
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setCanBorrow(true);
        user.setRole(com.getir.bootcamp.entity.Role.ROLE_PATRON);
        userRepository.save(user);

        // Create valid refresh token
        String refreshToken = jwtService.generateRefreshToken(user);
        String cookieName = tokenProperties.getRefreshToken().getCookieName();

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .cookie(new Cookie(cookieName, refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").value(refreshToken));
    }

    @Test
    void refreshToken_ShouldFail_WhenCookieMissing() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorMessage").value(ExceptionMessages.REFRESH_TOKEN_IS_MISSING));
    }
}
