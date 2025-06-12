package com.xavelo.sqs.adapter.out.mysql;

import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.adapter.out.mysql.QuoteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuoteMapper {
    @Mapping(target = "posts", constant = "0")
    @Mapping(target = "hits", constant = "0")
    QuoteEntity toEntity(Quote quote);

    Quote toDomain(QuoteEntity entity);
}
