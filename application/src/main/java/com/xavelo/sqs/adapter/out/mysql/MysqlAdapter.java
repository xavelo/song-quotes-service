package com.xavelo.sqs.adapter.out.mysql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.xavelo.sqs.adapter.Adapter;
import com.xavelo.sqs.adapter.CountAdapterInvocation;
import com.xavelo.sqs.adapter.out.mysql.spotify.SpotifyArtistMetadataEntity;
import com.xavelo.sqs.adapter.out.mysql.spotify.SpotifyArtistMetadataRepository;
import com.xavelo.sqs.application.domain.Artist;
import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.application.service.QuoteService;
import com.xavelo.sqs.port.out.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.xavelo.sqs.adapter.AdapterMetrics.Direction.OUT;
import static com.xavelo.sqs.adapter.AdapterMetrics.Type.DATABASE;

@Adapter
public class MysqlAdapter implements StoreQuotePort, LoadQuotePort, DeleteQuotePort, QuotesCountPort, IncrementPostsPort, IncrementHitsPort, LoadArtistQuoteCountsPort, UpdateQuotePort, LoadTop10QuotesPort, PatchQuotePort, LoadArtistPort, SyncArtistMetadataPort, ResetQuoteHitsPort, ResetQuotePostsPort {

    private static final Logger logger = LogManager.getLogger(MysqlAdapter.class);

    private final QuoteRepository quoteRepository;
    private final QuoteMapper quoteMapper;
    private final SpotifyArtistMetadataRepository spotifyArtistMetadataRepository;
    private final ObjectMapper objectMapper;

    public MysqlAdapter(QuoteRepository quoteRepository, QuoteMapper quoteMapper, SpotifyArtistMetadataRepository spotifyArtistMetadataRepository, ObjectMapper objectMapper) {
        this.quoteRepository = quoteRepository;
        this.quoteMapper = quoteMapper;
        this.spotifyArtistMetadataRepository = spotifyArtistMetadataRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @CountAdapterInvocation(name = "store-quote", direction = OUT, type = DATABASE)
    public Long storeQuote(Quote quote, Artist artistMetadata) {
        QuoteEntity entity = quoteMapper.toEntity(quote);
        if (artistMetadata != null) {
            entity.setSpotifyArtistId(artistMetadata.id());
            saveArtistMetadataIfNotExists(artistMetadata);
        }
        QuoteEntity saved = quoteRepository.save(entity);
        return saved.getId();
    }

    private void saveArtistMetadataIfNotExists(Artist artist) {
        if (spotifyArtistMetadataRepository.existsById(artist.id())) {
            return;
        }
        try {
            SpotifyArtistMetadataEntity metadataEntity = createMetadataEntity(artist);
            spotifyArtistMetadataRepository.save(metadataEntity);
        } catch (Exception e) {
            logger.error("Error saving artist metadata: {}",e.getMessage(), e);
        }
    }

    @Override
    @CountAdapterInvocation(name = "sync-artist-metadata", direction = OUT, type = DATABASE)
    public void syncArtistMetadata(String artistName, Artist artistMetadata) {
        if (artistMetadata == null || artistMetadata.id() == null) {
            logger.warn("Cannot sync metadata for artist {} due to missing Spotify id", artistName);
            return;
        }

        saveArtistMetadataIfNotExists(artistMetadata);
        quoteRepository.assignSpotifyArtistId(artistName, artistMetadata.id());
    }

    private SpotifyArtistMetadataEntity createMetadataEntity(Artist artist) throws com.fasterxml.jackson.core.JsonProcessingException {
        String genresJson = artist.genres() != null ? objectMapper.writeValueAsString(artist.genres()) : null;
        String topTracksJson = artist.topTracks() != null ? objectMapper.writeValueAsString(artist.topTracks()) : null;
        
        return new SpotifyArtistMetadataEntity(
                artist.id(),
                artist.name(),
                genresJson,
                artist.popularity(),
                artist.imageUrl(),
                artist.spotifyUrl(),
                topTracksJson
        );
    }

    @Override
    @CountAdapterInvocation(name = "store-quotes", direction = OUT, type = DATABASE)
    public java.util.List<Long> storeQuotes(List<Quote> quotes) {
        java.util.List<QuoteEntity> entities = quotes.stream()
                .map(quoteMapper::toEntity)
                .toList();
        java.util.List<QuoteEntity> saved = quoteRepository.saveAll(entities);
        return saved.stream().map(QuoteEntity::getId).toList();
    }

    @Override
    @CountAdapterInvocation(name = "load-quotes", direction = OUT, type = DATABASE)
    public java.util.List<Quote> loadQuotes() {
        java.util.List<QuoteEntity> entities = quoteRepository.findAll();
        return entities.stream()
                .map(quoteMapper::toDomain)
                .toList();
    }

    @Override
    @CountAdapterInvocation(name = "load-quote", direction = OUT, type = DATABASE)
    public Quote loadQuote(Long id) {
        return quoteRepository.findById(id)
                .map(quoteMapper::toDomain)
                .orElse(null);
    }

    @Override
    @CountAdapterInvocation(name = "load-random-quote", direction = OUT, type = DATABASE)
    public Quote loadRandomQuote() {
        QuoteEntity entity = quoteRepository.findRandomQuote();
        if (entity == null) {
            return null;
        }
        return quoteMapper.toDomain(entity);
    }

    @Override
    @CountAdapterInvocation(name = "delete-quote", direction = OUT, type = DATABASE)
    public void deleteQuote(Long id) {
        quoteRepository.deleteById(id);
    }

    @Override
    @CountAdapterInvocation(name = "count-quotes", direction = OUT, type = DATABASE)
    public Long countQuotes() {
        return quoteRepository.count();
    }

    @Override
    @CountAdapterInvocation(name = "increment-posts", direction = OUT, type = DATABASE)
    public void incrementPosts(Long id) {
        quoteRepository.incrementPosts(id);
    }

    @Override
    @CountAdapterInvocation(name = "increment-hits", direction = OUT, type = DATABASE)
    public void incrementHits(Long id) {
        quoteRepository.incrementHits(id);
    }

    @Override
    @CountAdapterInvocation(name = "reset-quote-hits", direction = OUT, type = DATABASE)
    public void resetQuoteHits() {
        quoteRepository.resetHits();
    }

    @Override
    @CountAdapterInvocation(name = "reset-quote-posts", direction = OUT, type = DATABASE)
    public void resetQuotePosts() {
        quoteRepository.resetPosts();
    }

    @Override
    @CountAdapterInvocation(name = "load-artist-quote-counts", direction = OUT, type = DATABASE)
    public java.util.List<ArtistQuoteCount> loadArtistQuoteCounts() {
        java.util.List<ArtistQuoteCountView> views = quoteRepository.findArtistQuoteCounts();
        return views.stream()
                .map(v -> new ArtistQuoteCount(v.getId(), v.getArtist(), v.getQuotes()))
                .toList();
    }

    @Override
    @CountAdapterInvocation(name = "update-quote", direction = OUT, type = DATABASE)
    public void updateQuote(Quote quote) {
        QuoteEntity entity = quoteRepository.findById(quote.id()).orElse(null);
        if (entity != null) {
            entity.setQuote(quote.quote());
            entity.setSong(quote.song());
            entity.setAlbum(quote.album());
            entity.setYear(quote.year());
            entity.setArtist(quote.artist());
            quoteRepository.save(entity);
        }
    }

    @Override
    @CountAdapterInvocation(name = "load-top10-quotes", direction = OUT, type = DATABASE)
    public List<Quote> loadTop10Quotes() {
        List<QuoteEntity> entities = quoteRepository.findTop10ByOrderByHitsDesc();
        return entities.stream()
                .map(quoteMapper::toDomain)
                .toList();
    }

    @Override
    @CountAdapterInvocation(name = "patch-quote", direction = OUT, type = DATABASE)
    public void patchQuote(Long id, Quote quote) {
        QuoteEntity entity = quoteRepository.findById(id).orElse(null);
        if (entity != null) {
            if (quote.quote() != null) {
                entity.setQuote(quote.quote());
            }
            if (quote.song() != null) {
                entity.setSong(quote.song());
            }
            if (quote.album() != null) {
                entity.setAlbum(quote.album());
            }
            if (quote.year() != null) {
                entity.setYear(quote.year());
            }
            if (quote.artist() != null) {
                entity.setArtist(quote.artist());
            }
            if (quote.hits() != null) {
                entity.setHits(quote.hits());
            }
            if (quote.posts() != null) {
                entity.setPosts(quote.posts());
            }
            quoteRepository.save(entity);
        }
    }

    @Override
    public Artist loadArtist(String id) {
        return spotifyArtistMetadataRepository.findById(id)
                .map(e -> {
                    try {
                        java.util.List<String> genres = e.getGenres() != null ? objectMapper.readValue(e.getGenres(), new TypeReference<java.util.List<String>>() {}) : null;
                        java.util.List<Artist.Track> topTracks = e.getTopTracks() != null ? objectMapper.readValue(e.getTopTracks(), new TypeReference<java.util.List<Artist.Track>>() {}) : null;
                        return new Artist(
                                e.getSpotifyArtistId(),
                                e.getName(),
                                genres,
                                e.getPopularity(),
                                e.getImageUrl(),
                                e.getSpotifyUrl(),
                                topTracks
                        );
                    } catch (Exception ex) {
                        logger.error("Error loading artist metadata: {}", ex.getMessage(), ex);
                        return null;
                    }
                })
                .orElse(null);
    }
}