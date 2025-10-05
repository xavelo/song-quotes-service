package com.xavelo.sqs.adapter.in.http.admin;

import com.xavelo.common.metrics.Adapter;
import com.xavelo.common.metrics.CountAdapterInvocation;
import com.xavelo.sqs.adapter.in.http.admin.mapper.AdminQuoteMapper;
import com.xavelo.sqs.application.api.AdminApi;
import com.xavelo.sqs.application.api.model.QuoteDto;
import com.xavelo.sqs.application.api.model.UpdateOutboxBatchSizeRequestDto;
import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.application.service.AdminService;
import com.xavelo.sqs.application.service.QuoteHelper;
import com.xavelo.sqs.port.in.PatchQuoteUseCase;
import com.xavelo.sqs.port.in.ResetQuoteHitsUseCase;
import com.xavelo.sqs.port.in.ResetQuotePostsUseCase;
import com.xavelo.sqs.port.in.StoreQuoteUseCase;
import com.xavelo.sqs.port.in.UpdateQuoteUseCase;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.xavelo.common.metrics.AdapterMetrics.Direction.IN;
import static com.xavelo.common.metrics.AdapterMetrics.Type.HTTP;

@Adapter
@RestController
public class AdminController implements AdminApi {

    private final AdminService adminService;
    private final StoreQuoteUseCase storeQuoteUseCase;
    private final UpdateQuoteUseCase updateQuoteUseCase;
    private final PatchQuoteUseCase patchQuoteUseCase;
    private final ResetQuoteHitsUseCase resetQuoteHitsUseCase;
    private final ResetQuotePostsUseCase resetQuotePostsUseCase;
    private final AdminQuoteMapper quoteMapper;

    private static final Logger logger = LogManager.getLogger(AdminController.class);

    public AdminController(AdminService adminService,
                           StoreQuoteUseCase storeQuoteUseCase,
                           UpdateQuoteUseCase updateQuoteUseCase,
                           PatchQuoteUseCase patchQuoteUseCase,
                           ResetQuoteHitsUseCase resetQuoteHitsUseCase,
                           ResetQuotePostsUseCase resetQuotePostsUseCase,
                           AdminQuoteMapper quoteMapper) {
        this.adminService = adminService;
        this.storeQuoteUseCase = storeQuoteUseCase;
        this.updateQuoteUseCase = updateQuoteUseCase;
        this.patchQuoteUseCase = patchQuoteUseCase;
        this.resetQuoteHitsUseCase = resetQuoteHitsUseCase;
        this.resetQuotePostsUseCase = resetQuotePostsUseCase;
        this.quoteMapper = quoteMapper;
    }

    @Override
    @CountAdapterInvocation(name = "create-quote", direction = IN, type = HTTP)
    public ResponseEntity<UUID> createQuote(@Valid @RequestBody QuoteDto quoteDto) {
        Quote quote = quoteMapper.toDomain(quoteDto);
        String id = storeQuoteUseCase.storeQuote(quote);
        logger.debug("returning id {}", id);
        return ResponseEntity.ok(UUID.fromString(id));
    }

    @Override
    @CountAdapterInvocation(name = "create-quotes", direction = IN, type = HTTP)
    public ResponseEntity<List<UUID>> createQuotes(@Valid @RequestBody List<@Valid QuoteDto> quoteDtos) {
        List<Quote> quotes = quoteMapper.toDomain(quoteDtos);
        List<String> ids = storeQuoteUseCase.storeQuotes(quotes);
        List<UUID> uuids = ids.stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
        return ResponseEntity.ok(uuids);
    }

    @Override
    @CountAdapterInvocation(name = "delete-quote", direction = IN, type = HTTP)
    public ResponseEntity<Void> deleteQuote(@PathVariable UUID id) {
        adminService.deleteQuote(id.toString());
        return ResponseEntity.noContent().build();
    }

    @Override
    @CountAdapterInvocation(name = "update-quote", direction = IN, type = HTTP)
    public ResponseEntity<Void> updateQuote(@PathVariable UUID id, @Valid @RequestBody QuoteDto quoteDto) {
        Quote quote = quoteMapper.toDomain(quoteDto);
        if (containsRestrictedFields(quote)) {
            return ResponseEntity.badRequest().build();
        }
        updateQuoteUseCase.updateQuote(QuoteHelper.withId(quote, id.toString()));
        return ResponseEntity.noContent().build();
    }

    private boolean containsRestrictedFields(Quote quote) {
        return quote.posts() != null || quote.hits() != null;
    }

    @Override
    @CountAdapterInvocation(name = "patch-quote", direction = IN, type = HTTP)
    public ResponseEntity<Void> patchQuote(@PathVariable UUID id, @Valid @RequestBody QuoteDto quoteDto) {
        Quote quote = quoteMapper.toDomain(quoteDto);
        if (containsRestrictedFields(quote)) {
            return ResponseEntity.badRequest().build();
        }
        patchQuoteUseCase.patchQuote(id.toString(), quote);
        return ResponseEntity.noContent().build();
    }

    @Override
    @CountAdapterInvocation(name = "update-outbox-worker-batch-size", direction = IN, type = HTTP)
    public ResponseEntity<Void> updateOutboxWorkerBatchSize(@Valid @RequestBody UpdateOutboxBatchSizeRequestDto updateOutboxBatchSizeRequestDto) {
        int batchSize = updateOutboxBatchSizeRequestDto.getBatchSize();
        adminService.updateOutboxWorkerBatchSize(batchSize);
        return ResponseEntity.noContent().build();
    }

    @Override
    @CountAdapterInvocation(name = "export-quotes", direction = IN, type = HTTP)
    public ResponseEntity<String> exportQuotes() {
        String sql = adminService.exportQuotesAsSql();
        return ResponseEntity.ok(sql);
    }

    @Override
    @CountAdapterInvocation(name = "reset-quote-hits", direction = IN, type = HTTP)
    public ResponseEntity<Void> resetQuoteHits() {
        resetQuoteHitsUseCase.resetQuoteHits();
        return ResponseEntity.noContent().build();
    }

    @Override
    @CountAdapterInvocation(name = "reset-quote-posts", direction = IN, type = HTTP)
    public ResponseEntity<Void> resetQuotePosts() {
        resetQuotePostsUseCase.resetQuotePosts();
        return ResponseEntity.noContent().build();
    }

}
