package com.getir.bootcamp.dto.request;

import com.getir.bootcamp.exception.ExceptionMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record BookRequest(
        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        @Size(max = 255, message = ExceptionMessages.FIELD_MUST_BE_AT_MOST_255_CHARACTERS)
        String title,

        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        @Size(max = 255, message = ExceptionMessages.FIELD_MUST_BE_AT_MOST_255_CHARACTERS)
        String author,

        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        @Size(max = 13, message = ExceptionMessages.FIELD_MUST_BE_AT_MOST_13_CHARACTERS)
        String isbn,

        @NotNull(message = ExceptionMessages.FIELD_IS_REQUIRED)
        LocalDate publicationDate,

        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        String genre
) {
}
