package com.getir.bootcamp.service;

import com.getir.bootcamp.dto.request.UserRequest;
import com.getir.bootcamp.dto.response.UserResponse;
import com.getir.bootcamp.entity.Role;
import com.getir.bootcamp.entity.User;
import com.getir.bootcamp.exception.ExceptionMessages;
import com.getir.bootcamp.exception.ResourceNotFoundException;
import com.getir.bootcamp.mapper.UserMapper;
import com.getir.bootcamp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequest userRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest(
                "john_doe",
                "John",
                "Doe"
        );

        user = new User();
        user.setUsername("john_doe");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setCanBorrow(true);
        user.setRole(Role.ROLE_PATRON);

        userResponse = new UserResponse(
                "john_doe",
                "John",
                "Doe",
                true,
                Role.ROLE_PATRON
        );
    }

    @Test
    void getUserDetailsService_ShouldReturnUserDetails_WhenUserExists() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.getUserDetailsService().loadUserByUsername("john_doe");

        assertNotNull(userDetails);
        assertEquals("john_doe", userDetails.getUsername());
    }

    @Test
    void getUserDetailsService_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByUsername("jane_doe")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.getUserDetailsService().loadUserByUsername("jane_doe"));
    }

    @Test
    void getUserEntityByUsername_ShouldReturnUser_WhenFound() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        User result = userService.getUserEntityByUsername("john_doe");

        assertNotNull(result);
        assertEquals("john_doe", result.getUsername());
    }

    @Test
    void getUserEntityByUsername_ShouldThrow_WhenNotFound() {
        when(userRepository.findByUsername("non_existent")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserEntityByUsername("non_existent"));

        assertEquals(ExceptionMessages.USER_NOT_FOUND, exception.getMessage());
    }

    @Test
    void getUserByUsername_ShouldReturnMappedUserResponse() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(userMapper.userEntityToUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserByUsername("john_doe");

        assertNotNull(result);
        assertEquals("john_doe", result.username());
        assertEquals("John", result.firstName());
    }

    @Test
    void updateUser_ShouldMapAndSaveAndReturnUserResponse() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUserEntityFromUserRequest(userRequest, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.userEntityToUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.updateUser("john_doe", userRequest);

        assertEquals("john_doe", result.username());
        verify(userMapper).updateUserEntityFromUserRequest(userRequest, user);
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_ShouldDelete_WhenFound() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        userService.deleteUser("john_doe");

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_ShouldThrow_WhenNotFound() {
        when(userRepository.findByUsername("not_found")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser("not_found"));
    }

    @Test
    void setUserRoleLibrarian_ShouldUpdateRoleAndReturnUserResponse() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.userEntityToUserResponse(user)).thenReturn(
                new UserResponse(
                        "john_doe",
                        "John",
                        "Doe",
                        true,
                        Role.ROLE_LIBRARIAN
                )
        );

        UserResponse result = userService.setUserRoleLibrarian("john_doe");

        assertEquals(Role.ROLE_LIBRARIAN, result.role());
        verify(userRepository).save(user);
    }

    @Test
    void setUserBorrowability_ShouldUpdateAndSave() {
        userService.setUserBorrowability(user, false);

        assertFalse(user.getCanBorrow());
        verify(userRepository).save(user);
    }
}
