package com.xavelo.sqs.adapter.in.http.secure;

import com.xavelo.adaptermetrics.Adapter;
import com.xavelo.adaptermetrics.CountAdapterInvocation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.xavelo.adaptermetrics.AdapterMetrics.Direction.IN;
import static com.xavelo.adaptermetrics.AdapterMetrics.Type.HTTP;

@Adapter
@RestController
@RequestMapping("/api/secure")
public class SecurePingController {

    @Value("${HOSTNAME:unknown}")
    private String podName;

    @GetMapping("/ping")
    @CountAdapterInvocation(name = "secure-ping", direction = IN, type = HTTP)
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong from " + podName);
    }

}

