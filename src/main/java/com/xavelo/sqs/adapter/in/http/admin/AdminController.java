package com.xavelo.sqs.adapter.in.http.admin;

import com.xavelo.sqs.application.service.AdminService;
import com.xavelo.sqs.port.in.DeleteQuoteUseCase;
import com.xavelo.sqs.port.in.StoreQuoteUseCase;
import com.xavelo.sqs.port.in.UpdateQuoteUseCase;
import com.xavelo.sqs.port.in.PatchQuoteUseCase;
import com.xavelo.sqs.application.domain.Quote;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.xavelo.sqs.application.service.QuoteHelper;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final StoreQuoteUseCase storeQuoteUseCase;
    private final DeleteQuoteUseCase deleteQuoteUseCase;
    private final UpdateQuoteUseCase updateQuoteUseCase;
    private final PatchQuoteUseCase patchQuoteUseCase;

    public AdminController(AdminService adminService,
                           StoreQuoteUseCase storeQuoteUseCase,
                           DeleteQuoteUseCase deleteQuoteUseCase,
                           UpdateQuoteUseCase updateQuoteUseCase,
                           PatchQuoteUseCase patchQuoteUseCase) {
        this.adminService = adminService;
        this.storeQuoteUseCase = storeQuoteUseCase;
        this.deleteQuoteUseCase = deleteQuoteUseCase;
        this.updateQuoteUseCase = updateQuoteUseCase;
        this.patchQuoteUseCase = patchQuoteUseCase;
    }

    @PostMapping("/quote")
    public ResponseEntity<Long> createQuote(@RequestBody Quote quote) {
        Long id = storeQuoteUseCase.storeQuote(quote);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/quotes")
    public ResponseEntity<java.util.List<Long>> createQuotes(@RequestBody java.util.List<Quote> quotes) {
        java.util.List<Long> ids = storeQuoteUseCase.storeQuotes(quotes);
        return ResponseEntity.ok(ids);
    }

    @DeleteMapping("/quote/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        deleteQuoteUseCase.deleteQuote(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/quote/{id}")
    public ResponseEntity<Void> updateQuote(@PathVariable Long id, @RequestBody Quote quote) {
        updateQuoteUseCase.updateQuote(QuoteHelper.withId(quote, id));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/quote/{id}")
    public ResponseEntity<Void> patchQuote(@PathVariable Long id, @RequestBody Quote quote) {
        patchQuoteUseCase.patchQuote(id, quote);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportQuotes() {
        String sql = adminService.exportQuotesAsSql();
        return ResponseEntity.ok(sql);
    }

}
