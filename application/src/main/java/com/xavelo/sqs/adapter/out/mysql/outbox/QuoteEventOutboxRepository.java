package com.xavelo.sqs.adapter.out.mysql.outbox;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface QuoteEventOutboxRepository extends JpaRepository<QuoteEventEntity, Long> {

    @Query("SELECT qe FROM QuoteEventEntity qe WHERE qe.status = :status AND qe.availableAt <= :availableAt ORDER BY qe.createdAt ASC")
    List<QuoteEventEntity> findPendingEvents(@Param("status") QuoteEventStatus status,
                                             @Param("availableAt") LocalDateTime availableAt,
                                             Pageable pageable);
}
