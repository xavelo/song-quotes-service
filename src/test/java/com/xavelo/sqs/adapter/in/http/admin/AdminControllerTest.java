package com.xavelo.sqs.adapter.in.http.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavelo.sqs.application.service.AdminService;
import com.xavelo.sqs.port.in.DeleteQuoteUseCase;
import com.xavelo.sqs.port.in.UpdateQuoteUseCase;
import com.xavelo.sqs.port.in.PatchQuoteUseCase;
import com.xavelo.sqs.application.domain.Quote;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;
    @MockBean private DeleteQuoteUseCase deleteQuoteUseCase;
    @MockBean private UpdateQuoteUseCase updateQuoteUseCase;
    @MockBean private PatchQuoteUseCase patchQuoteUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void exportQuotes() throws Exception {
        String expectedSql = "INSERT INTO quotes (id, quote, song, album, album_year, artist, hits, posts) VALUES (1, 'quote1', 'song1', 'album1', 2000, 'artist1', 0, 0);\n";
        when(adminService.exportQuotesAsSql()).thenReturn(expectedSql);

        mockMvc.perform(get("/admin/export"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedSql));
    }

    @Test
    void deleteQuote() throws Exception {
        mockMvc.perform(delete("/admin/quote/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateQuote() throws Exception {
        Quote quote = new Quote(null, "line", "song", "album", 1990, "artist", null, null);

        mockMvc.perform(put("/admin/quote/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quote)))
                .andExpect(status().isNoContent());

        verify(updateQuoteUseCase).updateQuote(new Quote(1L, "line", "song", "album", 1990, "artist", null, null));
    }

    @Test
    void patchQuote() throws Exception {
        Quote quote = new Quote(null, "line", null, null, null, null, null, null);

        mockMvc.perform(patch("/admin/quote/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quote)))
                .andExpect(status().isNoContent());

        verify(patchQuoteUseCase).patchQuote(1L, new Quote(null, "line", null, null, null, null, null, null));
    }
}