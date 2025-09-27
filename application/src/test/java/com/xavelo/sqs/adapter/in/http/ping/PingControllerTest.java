package com.xavelo.sqs.adapter.in.http.ping;

import com.xavelo.sqs.port.in.StoreQuoteUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PingController.class)
@TestPropertySource(properties = "HOSTNAME=testpod")
class PingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreQuoteUseCase storeQuoteUseCase;

    @Test
    void pingReturnsPong() throws Exception {
        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong from testpod"));
    }
}
