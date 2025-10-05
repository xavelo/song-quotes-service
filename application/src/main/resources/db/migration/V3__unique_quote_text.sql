ALTER TABLE quotes
    ADD CONSTRAINT uq_quotes_quote UNIQUE (quote);
