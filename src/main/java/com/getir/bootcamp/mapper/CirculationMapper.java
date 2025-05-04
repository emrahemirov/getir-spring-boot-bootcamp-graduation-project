package com.getir.bootcamp.mapper;

import com.getir.bootcamp.dto.response.CirculationResponse;
import com.getir.bootcamp.entity.Circulation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BookMapper.class})
public interface CirculationMapper {

    @Mapping(source = "user", target = "user")
    @Mapping(source = "book", target = "book")
    CirculationResponse ciraculationEntityToCirculationResponse(Circulation circulation);
}
