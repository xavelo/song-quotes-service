package com.xavelo.sqs.configuration;

import com.xavelo.sqs.adapter.out.mongo.MongoAdapter;
import com.xavelo.sqs.adapter.out.mongo.QuoteMongoMapper;
import com.xavelo.sqs.adapter.out.mongo.QuoteMongoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("mongo")
public class MongoAdapterConfiguration {

    @Bean
    public MongoAdapter mongoAdapter(QuoteMongoRepository quoteMongoRepository, QuoteMongoMapper quoteMongoMapper) {
        return new MongoAdapter(quoteMongoRepository, quoteMongoMapper);
    }
}
