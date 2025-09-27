package com.xavelo.sqs.port.out;

import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import java.util.List;

/**
 * Port for loading the number of quotes for each artist.
 */
public interface LoadArtistQuoteCountsPort {
    /**
     * @return list of artists with their quote count
     */
    List<ArtistQuoteCount> loadArtistQuoteCounts();
}
