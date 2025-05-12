package com.getir.bootcamp.dto.request;

import com.getir.bootcamp.exception.ExceptionMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Request payload for creating or updating a book")
public record BookRequest(

        @Schema(
                description = "Title of the book",
                example = "The Hobbit",
                maxLength = 255
        )
        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        @Size(max = 255, message = ExceptionMessages.FIELD_MUST_BE_AT_MOST_255_CHARACTERS)
        String title,

        @Schema(
                description = "Author of the book",
                example = "J.R.R. Tolkien",
                maxLength = 255
        )
        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        @Size(max = 255, message = ExceptionMessages.FIELD_MUST_BE_AT_MOST_255_CHARACTERS)
        String author,

        @Schema(
                description = "ISBN of the book (max 13 characters)",
                example = "9780261102217",
                maxLength = 13
        )
        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        @Size(max = 13, message = ExceptionMessages.FIELD_MUST_BE_AT_MOST_13_CHARACTERS)
        String isbn,

        @Schema(
                description = "Publication date of the book (ISO format)",
                example = "1937-09-21"
        )
        @NotNull(message = ExceptionMessages.FIELD_IS_REQUIRED)
        LocalDate publicationDate,

        @Schema(
                description = "Genre of the book",
                example = "Fantasy"
        )
        @NotBlank(message = ExceptionMessages.FIELD_MUST_NOT_BE_BLANK)
        String genre
) {
}
