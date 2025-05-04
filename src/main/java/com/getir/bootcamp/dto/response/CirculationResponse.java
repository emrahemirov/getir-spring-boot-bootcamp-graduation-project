package com.getir.bootcamp.dto.response;

import java.time.LocalDate;

public record CirculationResponse(
        Long id,
        UserResponse user,
        BookResponse book,
        LocalDate borrowDate,
        LocalDate dueDate,
        LocalDate returnDate,
        boolean isOverdue,
        boolean isReturned
) {
}
