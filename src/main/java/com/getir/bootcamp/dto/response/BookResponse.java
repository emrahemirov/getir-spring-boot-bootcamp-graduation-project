package com.getir.bootcamp.dto.response;

import java.time.LocalDate;

public record BookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        LocalDate publicationDate,
        String genre
) {
}