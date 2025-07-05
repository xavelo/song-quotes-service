package com.xavelo.sqs.adapter.out.mongo;

import com.xavelo.sqs.application.domain.Quote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface QuoteMongoMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "quote", target = "quote")
    @Mapping(source = "song", target = "song")
    @Mapping(source = "album", target = "album")
    @Mapping(source = "year", target = "year")
    @Mapping(source = "artist", target = "artist")
    @Mapping(source = "hits", target = "hits")
    @Mapping(source = "posts", target = "posts")
    QuoteMongoEntity toEntity(Quote quote);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "quote", target = "quote")
    @Mapping(source = "song", target = "song")
    @Mapping(source = "album", target = "album")
    @Mapping(source = "year", target = "year")
    @Mapping(source = "artist", target = "artist")
    @Mapping(source = "hits", target = "hits")
    @Mapping(source = "posts", target = "posts")
    Quote toDomain(QuoteMongoEntity quoteMongoEntity);
}
