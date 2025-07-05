package com.xavelo.sqs.adapter.in.http.admin;

import com.xavelo.sqs.port.in.ExportQuotesUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ExportQuotesUseCase exportQuotesUseCase;

    public AdminController(ExportQuotesUseCase exportQuotesUseCase) {
        this.exportQuotesUseCase = exportQuotesUseCase;
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportQuotes() {
        String sql = exportQuotesUseCase.exportQuotesAsSql();
        return ResponseEntity.ok(sql);
    }
}
