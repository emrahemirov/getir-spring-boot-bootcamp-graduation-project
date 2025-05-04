package com.getir.bootcamp.dto.request;

import com.getir.bootcamp.exception.ExceptionMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(

        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        @Size(max = 100, message = ExceptionMessages.FIELD_MUST_BE_AT_MOST_100_CHARACTERS)
        String firstName,

        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        @Size(max = 100, message = ExceptionMessages.FIELD_MUST_BE_AT_MOST_100_CHARACTERS)
        String lastName,

        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        @Size(min = 3, max = 50, message = ExceptionMessages.FIELD_MUST_BE_BETWEEN_3_AND_50_CHARACTERS)
        String username,

        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        @Size(min = 6, message = ExceptionMessages.PASSWORD_MUST_BE_AT_LEAST_6_CHARACTERS)
        String password
) {
}