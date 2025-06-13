package ebanking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ebanking.dto.PagedResponse;
import ebanking.dto.TransactionDTO;
import ebanking.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "查詢分頁交易 (需攜帶 JWT)")
    @GetMapping
    public ResponseEntity<PagedResponse<TransactionDTO>> getTransactionsByMonth(
            @Parameter(description = "查詢的年份，例如 2022", required = true)
            @RequestParam int year,
            @Parameter(description = "查詢的月份 (1~12)", required = true)
            @RequestParam int month,
            @Parameter(description = "第幾頁 (從 0 開始)", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁筆數", required = false)
            @RequestParam(defaultValue = "20") int size
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String accountIban = username;

        PagedResponse<TransactionDTO> response =
                transactionService.getTransactionsByMonth(accountIban, year, month, page, size);

        return ResponseEntity.ok(response);
    }
}
