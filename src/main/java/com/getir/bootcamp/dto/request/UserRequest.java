package com.getir.bootcamp.dto.request;

import com.getir.bootcamp.exception.ExceptionMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for updating user information")
public record UserRequest(

        @Schema(
                description = "Username of the user (3 to 50 characters)",
                example = "john_doe",
                minLength = 3,
                maxLength = 50
        )
        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        @Size(min = 3, max = 50, message = ExceptionMessages.FIELD_MUST_BE_BETWEEN_3_AND_50_CHARACTERS)
        String username,

        @Schema(
                description = "First name of the user (max 100 characters)",
                example = "John",
                maxLength = 100
        )
        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        @Size(max = 100, message = ExceptionMessages.FIELD_MUST_BE_AT_MOST_100_CHARACTERS)
        String firstName,

        @Schema(
                description = "Last name of the user (max 100 characters)",
                example = "Doe",
                maxLength = 100
        )
        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        @Size(max = 100, message = ExceptionMessages.FIELD_MUST_BE_AT_MOST_100_CHARACTERS)
        String lastName
) {
}
