package com.xavelo.sqs.adapter.out.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface QuoteMongoRepository extends MongoRepository<QuoteMongoEntity, Long> {

    @Query(value = "{}", sort = "{'hits': -1}", fields = "{}")
    List<QuoteMongoEntity> findTop10ByOrderByHitsDesc();
}
