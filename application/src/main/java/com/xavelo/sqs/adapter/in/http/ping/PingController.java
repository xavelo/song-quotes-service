package com.xavelo.sqs.adapter.in.http.ping;

import com.xavelo.common.metrics.Adapter;
import com.xavelo.common.metrics.CountAdapterInvocation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.xavelo.common.metrics.AdapterMetrics.Direction.IN;
import static com.xavelo.common.metrics.AdapterMetrics.Type.HTTP;

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

