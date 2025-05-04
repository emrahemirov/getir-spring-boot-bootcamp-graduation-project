package com.getir.bootcamp.mapper;

import com.getir.bootcamp.dto.response.CirculationResponse;
import com.getir.bootcamp.entity.Circulation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BookMapper.class})
public interface CirculationMapper {
    @Mapping(expression = "java(circulation.isReturned())", target = "isReturned")
    @Mapping(expression = "java(circulation.isOverdue())", target = "isOverdue")
    CirculationResponse ciraculationEntityToCirculationResponse(Circulation circulation);
}
