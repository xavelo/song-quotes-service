package com.xavelo.sqs.adapter.in.http.ping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PingController {

    @Value("${HOSTNAME:unknown}")
    private String podName;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong from " + podName);
    }
}

