package com.xavelo.sqs.port.out;

/**
 * Port for publishing application metrics.
 */
public interface MetricsPort {
    /**
     * Increment the hits counter when a quote is served.
     */
    void incrementHits();
}
