package com.xavelo.sqs.adapter.in.http.ping;

import com.xavelo.sqs.adapter.Adapter;
import com.xavelo.sqs.adapter.CountAdapterInvocation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.xavelo.sqs.adapter.AdapterMetrics.Direction.IN;
import static com.xavelo.sqs.adapter.AdapterMetrics.Type.HTTP;

@Adapter
@RestController
@RequestMapping("/api")
public class PingController {

    @Value("${HOSTNAME:unknown}")
    private String podName;

    @GetMapping("/ping")
    @CountAdapterInvocation(name = "ping", direction = IN, type = HTTP)
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong from " + podName);
    }
}

