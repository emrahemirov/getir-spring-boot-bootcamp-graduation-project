package com.getir.bootcamp.mapper;

import com.getir.bootcamp.dto.request.BookRequest;
import com.getir.bootcamp.dto.response.BookResponse;
import com.getir.bootcamp.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookResponse toResponse(Book book);

    Book toEntity(BookRequest bookRequest);

    void updateBookFromRequest(BookRequest bookRequest, @MappingTarget Book book);
}