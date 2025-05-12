package com.getir.bootcamp.dto.request;

import com.getir.bootcamp.exception.ExceptionMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for user sign-in")
public record SignInRequest(

        @Schema(
                description = "Username of the user",
                example = "john_doe"
        )
        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        String username,

        @Schema(
                description = "Password of the user",
                example = "P@ssw0rd123"
        )
        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        String password
) {
}
