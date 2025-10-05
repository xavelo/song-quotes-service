package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.port.in.ExportQuotesUseCase;
import com.xavelo.sqs.port.in.DeleteQuoteUseCase;
import com.xavelo.sqs.port.in.ResetQuoteHitsUseCase;
import com.xavelo.sqs.port.in.ResetQuotePostsUseCase;
import com.xavelo.sqs.port.in.UpdateQuoteUseCase;
import com.xavelo.sqs.port.out.DeleteQuotePort;
import com.xavelo.sqs.port.out.LoadQuotePort;
import com.xavelo.sqs.port.out.ResetQuoteHitsPort;
import com.xavelo.sqs.port.out.ResetQuotePostsPort;
import com.xavelo.sqs.port.out.UpdateQuotePort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AdminService implements ExportQuotesUseCase, DeleteQuoteUseCase, UpdateQuoteUseCase, ResetQuoteHitsUseCase, ResetQuotePostsUseCase {

    private final LoadQuotePort loadQuotePort;
    private final DeleteQuotePort deleteQuotePort;
    private final UpdateQuotePort updateQuotePort;
    private final ResetQuoteHitsPort resetQuoteHitsPort;
    private final ResetQuotePostsPort resetQuotePostsPort;
    private final QuoteEventRelayWorker quoteEventRelayWorker;

    public AdminService(LoadQuotePort loadQuotePort,
                        DeleteQuotePort deleteQuotePort,
                        UpdateQuotePort updateQuotePort,
                        ResetQuoteHitsPort resetQuoteHitsPort,
                        ResetQuotePostsPort resetQuotePostsPort,
                        QuoteEventRelayWorker quoteEventRelayWorker) {
        this.loadQuotePort = loadQuotePort;
        this.deleteQuotePort = deleteQuotePort;
        this.updateQuotePort = updateQuotePort;
        this.resetQuoteHitsPort = resetQuoteHitsPort;
        this.resetQuotePostsPort = resetQuotePostsPort;
        this.quoteEventRelayWorker = quoteEventRelayWorker;
    }

    @Override
    public List<Quote> exportQuotes() {
        return loadQuotePort.loadQuotes().stream()
                .map(QuoteHelper::sanitize)
                .toList();
    }

    @Override
    public void deleteQuote(UUID id) {
        deleteQuotePort.deleteQuote(id);
    }

    @Override
    public void updateQuote(Quote quote) {
        updateQuotePort.updateQuote(quote);
    }

    @Override
    public void resetQuoteHits() {
        resetQuoteHitsPort.resetQuoteHits();
    }

    @Override
    public void resetQuotePosts() {
        resetQuotePostsPort.resetQuotePosts();
    }

    public void updateOutboxWorkerBatchSize(int batchSize) {
        quoteEventRelayWorker.updateBatchSize(batchSize);
    }
}
