package com.getir.bootcamp.mapper;

import com.getir.bootcamp.dto.request.BookRequest;
import com.getir.bootcamp.dto.response.BookResponse;
import com.getir.bootcamp.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookResponse bookEntityToBookResponse(Book book);

    Book bookRequestToBookEntity(BookRequest bookRequest);

    void updateBookEntityFromRequest(BookRequest bookRequest, @MappingTarget Book book);
}