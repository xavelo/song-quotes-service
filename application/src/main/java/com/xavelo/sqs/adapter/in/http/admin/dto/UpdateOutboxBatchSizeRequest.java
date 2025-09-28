package com.xavelo.sqs.adapter.in.http.admin.dto;

import jakarta.validation.constraints.Min;

public record UpdateOutboxBatchSizeRequest(@Min(1) int batchSize) {
}
