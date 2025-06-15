package ebanking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ebanking.dto.PagedResponse;
import ebanking.dto.TransactionDTO;
import ebanking.security.CustomUserDetails;
import ebanking.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "分頁查詢指定月份的所有交易 (需攜帶 JWT)")
    @GetMapping("/transactions")
    public ResponseEntity<PagedResponse<TransactionDTO>> getTransactionsByMonth(
            @Parameter(description = "查詢的年份，例如 2022", required = true)
            @RequestParam int year,
            @Parameter(description = "查詢的月份 (1~12)", required = true)
            @RequestParam int month,
            @Parameter(
            	      description = "第幾頁 (從 0 開始)",
            	      required = false,
            	      schema = @Schema(defaultValue = "0")
            	    )
            @RequestParam(defaultValue = "0") int page,
            @Parameter(
            	      description = "每頁筆數",
            	      required = false,
            	      schema = @Schema(defaultValue = "20")
            	    )
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "可選：按單一 IBAN 過濾", required = false)
            @RequestParam(required = false) String accountIban   // optional
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth.getName();
        Object principal = auth.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new IllegalStateException("Principal is not CustomUserDetails");
        }
        CustomUserDetails user = (CustomUserDetails) principal;
        
        String customerId = user.getCustomerId();
        
        

        PagedResponse<TransactionDTO> response =
                transactionService.getTransactionsByMonth(
                		customerId, year, month, page, size, accountIban);
        
        
        return ResponseEntity.ok(response);
    }
}
