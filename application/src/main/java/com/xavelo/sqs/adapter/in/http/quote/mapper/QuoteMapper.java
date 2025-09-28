package com.xavelo.sqs.adapter.in.http.quote.mapper;

import com.xavelo.sqs.application.api.model.QuoteDto;
import com.xavelo.sqs.application.domain.Quote;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuoteMapper {

    QuoteDto toDto(Quote quote);

    List<QuoteDto> toDtos(List<Quote> quotes);
}
