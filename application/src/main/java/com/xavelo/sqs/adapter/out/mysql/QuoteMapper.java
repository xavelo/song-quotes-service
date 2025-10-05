package com.xavelo.sqs.adapter.out.mysql;

import com.xavelo.sqs.application.domain.Quote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface QuoteMapper {
    @Mapping(target = "posts", constant = "0")
    @Mapping(target = "hits", constant = "0")
    @Mapping(target = "spotifyArtistId", source = "spotifyArtistId")
    @Mapping(target = "id", expression = "java(quote.id() != null ? quote.id().toString() : null)")
    QuoteEntity toEntity(Quote quote);

    @Mapping(target = "id", expression = "java(entity.getId() != null ? UUID.fromString(entity.getId()) : null)")
    Quote toDomain(QuoteEntity entity);
}
