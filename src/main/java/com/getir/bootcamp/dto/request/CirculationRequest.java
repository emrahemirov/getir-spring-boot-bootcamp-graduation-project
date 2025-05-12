package com.getir.bootcamp.dto.request;

import com.getir.bootcamp.exception.ExceptionMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Request payload for borrowing a book")
public record CirculationRequest(

        @Schema(
                description = "ID of the book to borrow",
                example = "101"
        )
        @NotNull(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        Long bookId,

        @Schema(
                description = "Date when the book is borrowed (ISO format)",
                example = "2025-05-05"
        )
        @NotNull(message = ExceptionMessages.FIELD_IS_REQUIRED)
        LocalDate borrowDate,

        @Schema(
                description = "Due date for returning the book (must be a future date, ISO format)",
                example = "2025-06-05"
        )
        @NotNull(message = ExceptionMessages.FIELD_IS_REQUIRED)
        @Future(message = ExceptionMessages.FIELD_MUST_BE_A_FUTURE_DATE)
        LocalDate dueDate
) {
}
