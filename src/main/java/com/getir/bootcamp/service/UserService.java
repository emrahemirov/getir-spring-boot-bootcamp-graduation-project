package com.getir.bootcamp.service;

import com.getir.bootcamp.dto.request.UserRequest;
import com.getir.bootcamp.dto.response.UserResponse;
import com.getir.bootcamp.entity.Role;
import com.getir.bootcamp.entity.User;
import com.getir.bootcamp.exception.ExceptionMessages;
import com.getir.bootcamp.exception.ResourceNotFoundException;
import com.getir.bootcamp.mapper.UserMapper;
import com.getir.bootcamp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDetailsService getUserDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(ExceptionMessages.USER_NOT_FOUND));
    }

    public User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.USER_NOT_FOUND));
    }

    public UserResponse getUserByUsername(String username) {
        User user = getUserEntityByUsername(username);
        return userMapper.userEntityToUserResponse(user);
    }

    public UserResponse updateUser(String username, UserRequest userRequest) {
        User user = getUserEntityByUsername(username);
        userMapper.updateUserEntityFromUserRequest(userRequest, user);
        User savedUser = userRepository.save(user);
        return userMapper.userEntityToUserResponse(savedUser);
    }

    public void deleteUser(String username) {
        User user = getUserEntityByUsername(username);
        userRepository.delete(user);
    }

    public UserResponse setUserRoleLibrarian(String username) {
        User user = getUserEntityByUsername(username);
        user.setRole(Role.ROLE_LIBRARIAN);
        userRepository.save(user);
        return userMapper.userEntityToUserResponse(user);
    }

    public void setUserBorrowability(User user, Boolean canBorrow) {
        user.setCanBorrow(canBorrow);
        userRepository.save(user);
    }
}
