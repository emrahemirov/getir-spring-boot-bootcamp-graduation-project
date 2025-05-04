package com.getir.bootcamp.dto.request;

import com.getir.bootcamp.exception.ExceptionMessages;
import jakarta.validation.constraints.NotBlank;

public record SignInRequest(

        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        String username,

        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        String password
) {
}