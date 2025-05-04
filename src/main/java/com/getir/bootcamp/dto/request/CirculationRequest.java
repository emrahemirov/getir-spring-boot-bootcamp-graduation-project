package com.getir.bootcamp.dto.request;

import com.getir.bootcamp.exception.ExceptionMessages;
import jakarta.validation.constraints.NotNull;

public record CirculationRequest(
        @NotNull(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        Long bookId
) {
}