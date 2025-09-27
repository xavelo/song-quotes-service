package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.port.in.ExportQuotesUseCase;
import com.xavelo.sqs.port.in.DeleteQuoteUseCase;
import com.xavelo.sqs.port.in.UpdateQuoteUseCase;
import com.xavelo.sqs.port.out.DeleteQuotePort;
import com.xavelo.sqs.port.out.LoadQuotePort;
import com.xavelo.sqs.port.out.UpdateQuotePort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService implements ExportQuotesUseCase, DeleteQuoteUseCase, UpdateQuoteUseCase {

    private final LoadQuotePort loadQuotePort;
    private final DeleteQuotePort deleteQuotePort;
    private final UpdateQuotePort updateQuotePort;

    public AdminService(LoadQuotePort loadQuotePort, DeleteQuotePort deleteQuotePort, UpdateQuotePort updateQuotePort) {
        this.loadQuotePort = loadQuotePort;
        this.deleteQuotePort = deleteQuotePort;
        this.updateQuotePort = updateQuotePort;
    }

    @Override
    public String exportQuotesAsSql() {
        List<Quote> quotes = loadQuotePort.loadQuotes();
        StringBuilder sqlBuilder = new StringBuilder();
        for (Quote quote : quotes) {
            sqlBuilder.append(String.format(
                    "INSERT INTO quotes (id, quote, song, album, album_year, artist, hits, posts) VALUES (%d, '%s', '%s', '%s', %d, '%s', %d, %d);\n",
                    quote.id(),
                    quote.quote().replace("'", "''"),
                    quote.song().replace("'", "''"),
                    quote.album().replace("'", "''"),
                    quote.year(),
                    quote.artist().replace("'", "''"),
                    quote.hits(),
                    quote.posts()
            ));
        }
        return sqlBuilder.toString();
    }

    @Override
    public void deleteQuote(Long id) {
        deleteQuotePort.deleteQuote(id);
    }

    @Override
    public void updateQuote(Quote quote) {
        updateQuotePort.updateQuote(quote);
    }
}