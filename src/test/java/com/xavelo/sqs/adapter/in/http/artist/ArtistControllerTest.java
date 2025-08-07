package com.xavelo.sqs.adapter.in.http.artist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import com.xavelo.sqs.port.in.GetArtistQuoteCountsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArtistController.class)
class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private GetArtistQuoteCountsUseCase getArtistQuoteCountsUseCase;

    @Test
    void getArtists() throws Exception {
        List<ArtistQuoteCount> artists = List.of(new ArtistQuoteCount("art", 2L));
        when(getArtistQuoteCountsUseCase.getArtistQuoteCounts()).thenReturn(artists);

        mockMvc.perform(get("/api/artists"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(artists)));
    }
}
