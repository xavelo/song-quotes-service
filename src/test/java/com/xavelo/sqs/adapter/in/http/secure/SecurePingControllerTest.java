package com.xavelo.sqs.adapter.in.http.secure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecurePingController.class)
@TestPropertySource(properties = "HOSTNAME=testpod")
class SecurePingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void pingReturnsPong() throws Exception {
        mockMvc.perform(get("/api/secure/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong from testpod"));
    }
}
