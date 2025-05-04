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

    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.USER_NOT_FOUND));
    }

    public UserResponse getUserById(Long id) {
        User user = getUserEntityById(id);
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = getUserEntityById(id);
        userMapper.updateUserFromUserRequest(userRequest, user);
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }

    public void deleteUser(Long id) {
        User user = getUserEntityById(id);
        userRepository.delete(user);
    }

    public UserResponse setUserRoleLibrarian(Long id) {
        User user = getUserEntityById(id);
        user.setRole(Role.ROLE_LIBRARIAN);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
}
