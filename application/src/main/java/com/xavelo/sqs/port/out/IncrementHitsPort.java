package com.xavelo.sqs.port.out;

import java.util.UUID;

public interface IncrementHitsPort {
    void incrementHits(UUID id);
}
