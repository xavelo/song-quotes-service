package com.xavelo.sqs.adapter.in.http.ping;

import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.port.in.StoreQuoteUseCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PingController {

    private static final Logger logger = LogManager.getLogger(PingController.class);

    private final StoreQuoteUseCase storeQuoteUseCase;

    @Value("${HOSTNAME:unknown}")
    private String podName;

    public PingController(StoreQuoteUseCase storeQuoteUseCase) {
        this.storeQuoteUseCase = storeQuoteUseCase;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong from " + podName);
    }

//    @PostMapping("/quote")
//    public ResponseEntity<Long> createQuote(@RequestBody Quote quote) {
//        Long id = storeQuoteUseCase.storeQuote(quote);
//        return ResponseEntity.ok(id);
//    }

}

