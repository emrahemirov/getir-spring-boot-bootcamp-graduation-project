package com.getir.bootcamp.dto.request;

import com.getir.bootcamp.exception.ExceptionMessages;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CirculationRequest(

        @NotNull(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        Long bookId,

        @NotNull(message = ExceptionMessages.FIELD_IS_REQUIRED)
        LocalDate borrowDate,

        @NotNull(message = ExceptionMessages.FIELD_IS_REQUIRED)
        @Future(message = ExceptionMessages.FIELD_MUST_BE_A_FUTURE_DATE)
        LocalDate dueDate
) {
}
