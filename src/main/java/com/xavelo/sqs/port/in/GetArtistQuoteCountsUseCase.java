package com.xavelo.sqs.port.in;

import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import java.util.List;

/**
 * Use case for retrieving artists and the number of quotes for each of them.
 */
public interface GetArtistQuoteCountsUseCase {
    List<ArtistQuoteCount> getArtistQuoteCounts();
}
