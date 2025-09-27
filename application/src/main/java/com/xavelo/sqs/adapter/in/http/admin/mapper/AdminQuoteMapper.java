package com.xavelo.sqs.adapter.in.http.admin.mapper;

import com.xavelo.sqs.application.api.model.QuoteDto;
import com.xavelo.sqs.application.domain.Quote;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminQuoteMapper {

    Quote toDomain(QuoteDto quoteDto);

    List<Quote> toDomain(List<QuoteDto> quoteDtos);
}
