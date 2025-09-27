package com.xavelo.sqs.adapter.in.http.artist.mapper;

import com.xavelo.sqs.application.api.model.ArtistDto;
import com.xavelo.sqs.application.api.model.ArtistQuoteCountDto;
import com.xavelo.sqs.application.api.model.ArtistTrackDto;
import com.xavelo.sqs.application.domain.Artist;
import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import org.mapstruct.Mapper;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArtistMapper {

    ArtistDto toDto(Artist artist);

    ArtistTrackDto toTrackDto(Artist.Track track);

    ArtistQuoteCountDto toQuoteCountDto(ArtistQuoteCount artistQuoteCount);

    List<ArtistQuoteCountDto> toQuoteCountDtos(List<ArtistQuoteCount> artists);

    default JsonNullable<String> map(String value) {
        return value == null ? JsonNullable.undefined() : JsonNullable.of(value);
    }
}
