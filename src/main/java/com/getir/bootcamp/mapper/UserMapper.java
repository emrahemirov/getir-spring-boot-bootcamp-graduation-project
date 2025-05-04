package com.getir.bootcamp.mapper;

import com.getir.bootcamp.dto.request.SignUpRequest;
import com.getir.bootcamp.dto.request.UserRequest;
import com.getir.bootcamp.dto.response.UserResponse;
import com.getir.bootcamp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse userEntityToUserResponse(User user);

    void updateUserEntityFromUserRequest(UserRequest userRequestDto, @MappingTarget User user);

    User signUpRequestToUserEntity(SignUpRequest signUpRequest);
}