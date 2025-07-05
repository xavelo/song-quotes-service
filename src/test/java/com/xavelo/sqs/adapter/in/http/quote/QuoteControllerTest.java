package com.xavelo.sqs.adapter.in.http.quote;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.port.in.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
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
    @MockBean private GetArtistQuoteCountsUseCase getArtistQuoteCountsUseCase;
    @MockBean private UpdateQuoteUseCase updateQuoteUseCase;
    @MockBean private GetTop10QuotesUseCase getTop10QuotesUseCase;

    @Test
    void getQuotes() throws Exception {
        List<Quote> quotes = List.of(new Quote(1L, "q", "s", "a", 1999, "art", 0, 0));
        when(getQuotesUseCase.getQuotes()).thenReturn(quotes);

        mockMvc.perform(get("/api/quotes"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(quotes)));
    }

    @Test
    void getArtists() throws Exception {
        List<ArtistQuoteCount> artists = List.of(new ArtistQuoteCount("art", 2L));
        when(getArtistQuoteCountsUseCase.getArtistQuoteCounts()).thenReturn(artists);

        mockMvc.perform(get("/api/artists"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(artists)));
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
        Quote quote = new Quote(1L, "q", "s", "a", 1999, "art", 0, 0);
        when(getRandomQuoteUseCase.getRandomQuote()).thenReturn(quote);

        mockMvc.perform(get("/api/quote/random"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(quote)));
    }

    @Test
    void getQuoteFound() throws Exception {
        Quote quote = new Quote(1L, "q", "s", "a", 1999, "art", 0, 0);
        when(getQuoteUseCase.getQuote(1L)).thenReturn(quote);

        mockMvc.perform(get("/api/quote/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(quote)));
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
                new Quote(1L, "q1", "s1", "a1", 2000, "art1", 100, 10),
                new Quote(2L, "q2", "s2", "a2", 2001, "art2", 90, 9)
        );
        when(getTop10QuotesUseCase.getTop10Quotes()).thenReturn(quotes);

        mockMvc.perform(get("/api/quotes/top10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(quotes)));
    }
}
