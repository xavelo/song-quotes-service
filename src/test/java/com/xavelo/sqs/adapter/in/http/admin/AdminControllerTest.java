package com.xavelo.sqs.adapter.in.http.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavelo.sqs.port.in.ExportQuotesUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExportQuotesUseCase exportQuotesUseCase;

    @Test
    void exportQuotes() throws Exception {
        String expectedSql = "INSERT INTO quotes (id, quote, song, album, album_year, artist, hits, posts) VALUES (1, 'quote1', 'song1', 'album1', 2000, 'artist1', 0, 0);\n";
        when(exportQuotesUseCase.exportQuotesAsSql()).thenReturn(expectedSql);

        mockMvc.perform(get("/admin/quotes/export"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedSql));
    }
}
