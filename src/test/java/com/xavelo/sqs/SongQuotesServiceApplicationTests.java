package com.xavelo.sqs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import io.micrometer.core.instrument.MeterRegistry;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SongQuotesServiceApplicationTests {

	@Autowired
        private ApplicationContext applicationContext;

        @Autowired
        private MeterRegistry meterRegistry;

        @Test
        void contextLoads() {
                assertNotNull(applicationContext);
                assertNotNull(meterRegistry);
        }

}
