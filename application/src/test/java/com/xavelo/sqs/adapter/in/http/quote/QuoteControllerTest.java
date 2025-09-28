package com.xavelo.sqs.adapter.in.http.quote;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavelo.sqs.adapter.in.http.quote.mapper.QuoteMapper;
import com.xavelo.sqs.application.api.model.QuoteDto;
import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.port.in.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuoteController.class)
class QuoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private StoreQuoteUseCase storeQuoteUseCase;
    @MockBean private GetQuotesUseCase getQuotesUseCase;
    @MockBean private GetQuoteUseCase getQuoteUseCase;
    @MockBean private GetRandomQuoteUseCase getRandomQuoteUseCase;
    @MockBean private CountQuotesUseCase countQuotesUseCase;
    @MockBean private DeleteQuoteUseCase deleteQuoteUseCase;
    @MockBean private UpdateQuoteUseCase updateQuoteUseCase;
    @MockBean private GetTop10QuotesUseCase getTop10QuotesUseCase;
    @MockBean private QuoteMapper quoteMapper;

    @Test
    void getQuotes() throws Exception {
        List<Quote> quotes = List.of(new Quote(1L, "q", "s", "a", 1999, "art", 0, 0, null));
        List<QuoteDto> dtos = List.of(new QuoteDto()
                .id(1L)
                .quote("q")
                .song("s")
                .album("a")
                .year(1999)
                .artist("art")
                .posts(0)
                .hits(0)
                .spotifyArtistId(null));
        when(getQuotesUseCase.getQuotes()).thenReturn(quotes);
        when(quoteMapper.toDtos(quotes)).thenReturn(dtos);

        mockMvc.perform(get("/api/quotes"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
    }

    @Test
    void getQuotesCount() throws Exception {
        when(countQuotesUseCase.countQuotes()).thenReturn(5L);

        mockMvc.perform(get("/api/quotes/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void getRandomQuoteFound() throws Exception {
        Quote quote = new Quote(1L, "q", "s", "a", 1999, "art", 0, 0, null);
        QuoteDto dto = new QuoteDto()
                .id(1L)
                .quote("q")
                .song("s")
                .album("a")
                .year(1999)
                .artist("art")
                .posts(0)
                .hits(0)
                .spotifyArtistId(null);
        when(getRandomQuoteUseCase.getRandomQuote()).thenReturn(quote);
        when(quoteMapper.toDto(quote)).thenReturn(dto);

        mockMvc.perform(get("/api/quote/random"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void getQuoteFound() throws Exception {
        Quote quote = new Quote(1L, "q", "s", "a", 1999, "art", 0, 0, null);
        QuoteDto dto = new QuoteDto()
                .id(1L)
                .quote("q")
                .song("s")
                .album("a")
                .year(1999)
                .artist("art")
                .posts(0)
                .hits(0)
                .spotifyArtistId(null);
        when(getQuoteUseCase.getQuote(1L)).thenReturn(quote);
        when(quoteMapper.toDto(quote)).thenReturn(dto);

        mockMvc.perform(get("/api/quote/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void getQuoteNotFound() throws Exception {
        when(getQuoteUseCase.getQuote(2L)).thenReturn(null);

        mockMvc.perform(get("/api/quote/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTop10Quotes() throws Exception {
        List<Quote> quotes = List.of(
                new Quote(1L, "q1", "s1", "a1", 2000, "art1", 100, 10, null),
                new Quote(2L, "q2", "s2", "a2", 2001, "art2", 90, 9, null)
        );
        List<QuoteDto> dtos = List.of(
                new QuoteDto()
                        .id(1L)
                        .quote("q1")
                        .song("s1")
                        .album("a1")
                        .year(2000)
                        .artist("art1")
                        .posts(100)
                        .hits(10)
                        .spotifyArtistId(null),
                new QuoteDto()
                        .id(2L)
                        .quote("q2")
                        .song("s2")
                        .album("a2")
                        .year(2001)
                        .artist("art2")
                        .posts(90)
                        .hits(9)
                        .spotifyArtistId(null)
        );
        when(getTop10QuotesUseCase.getTop10Quotes()).thenReturn(quotes);
        when(quoteMapper.toDtos(quotes)).thenReturn(dtos);

        mockMvc.perform(get("/api/quotes/top10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
    }
}
