package com.getir.bootcamp.mapper;

import com.getir.bootcamp.dto.request.SignUpRequest;
import com.getir.bootcamp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

    User signUpRequestToUser(SignUpRequest signUpRequest);
    SignUpRequest userToSignUpRequest(User user);
}