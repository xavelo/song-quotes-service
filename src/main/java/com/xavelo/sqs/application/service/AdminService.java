package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.port.in.ExportQuotesUseCase;
import com.xavelo.sqs.port.out.LoadQuotePort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService implements ExportQuotesUseCase {

    private final LoadQuotePort loadQuotePort;

    public AdminService(LoadQuotePort loadQuotePort) {
        this.loadQuotePort = loadQuotePort;
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
}
