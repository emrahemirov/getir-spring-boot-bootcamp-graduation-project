package com.getir.bootcamp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getir.bootcamp.dto.request.UserRequest;
import com.getir.bootcamp.entity.Role;
import com.getir.bootcamp.entity.User;
import com.getir.bootcamp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = new User();
        user.setUsername("john_doe");
        user.setPassword("encoded_pass");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setCanBorrow(true);
        user.setRole(Role.ROLE_PATRON);
        userRepository.save(user);
    }

    @Test
    @WithMockUser(authorities = "ROLE_LIBRARIAN")
    void getUserById_ShouldReturnUserDetails() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("john_doe"))
                .andExpect(jsonPath("$.data.canBorrow").value(true));
    }

    @Test
    @WithMockUser(authorities = "ROLE_LIBRARIAN")
    void updateUser_ShouldModifyAndReturnUser() throws Exception {
        UserRequest request = new UserRequest("john_doe", "Johnny", "Doeman");

        mockMvc.perform(put("/api/v1/users/" + user.getUsername())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Johnny"))
                .andExpect(jsonPath("$.data.lastName").value("Doeman"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_LIBRARIAN")
    void deleteUser_ShouldRemoveUser() throws Exception {
        mockMvc.perform(delete("/api/v1/users/" + user.getUsername()))
                .andExpect(status().isOk());

        assertFalse(userRepository.existsByUsername(user.getUsername()));
    }

    @Test
    @WithMockUser(authorities = "ROLE_LIBRARIAN")
    void setUserRoleLibrarian_ShouldPromoteUser() throws Exception {
        mockMvc.perform(patch("/api/v1/users/" + user.getUsername() + "/set-librarian"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("ROLE_LIBRARIAN"));
    }
}
