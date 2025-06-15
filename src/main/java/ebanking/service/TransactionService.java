// src/main/java/ebanking/service/TransactionService.java
package ebanking.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import ebanking.dto.PagedResponse;
import ebanking.dto.TransactionDTO;
import ebanking.mapper.TransactionMapper;
import ebanking.repository.TransactionRepository;

@Service
public class TransactionService {

//    private final TransactionRepository transactionRepository;
    private final TransactionMapper   transactionMapper;
    private final ModelMapper modelMapper;
    private final ExchangeRateService exchangeRateService;
    private final UserService            userService;
    
    public TransactionService(TransactionMapper transactionMapper,
            ModelMapper modelMapper,
            ExchangeRateService exchangeRateService,
            UserService userService) {
		this.transactionMapper = transactionMapper;
		this.modelMapper = modelMapper;
		this.exchangeRateService = exchangeRateService;
		this.userService         = userService;
	}

    public PagedResponse<TransactionDTO> getTransactionsByMonth(
    		String customerId,
            int year,
            int month,
            int page,
            int size,
            String accountIban// optional, 可為 null
            
    ) {
    	
    	
        Map<String,Object> params = new HashMap<>();
        params.put("customerId",  customerId);
        params.put("year",        year);
        params.put("month",       month);
        params.put("page",        page);
        params.put("size",        size);
        params.put("accountIban", accountIban);
    	
        List<TransactionDTO>  content       = transactionMapper.findByCustomerAndMonth(params);
        int                   totalElements = transactionMapper.countByCustomerAndMonth(params);    	
    	
        // 5. Call 匯率服務，計算各筆的本位幣金額
        content.forEach(dto -> {
            BigDecimal rate = exchangeRateService
                                 .getRate(dto.getCurrency(), ExchangeRateService.BASE_CURRENCY)
                                 .max(BigDecimal.ZERO);
            dto.setAmountInBaseCurrency(
                dto.getAmount().multiply(rate)
            );
        });

//        // 5. 計算分頁小計
//        //指定分頁裡面，每筆交易原幣別的分頁總計：
//        BigDecimal sumOriginal = content.stream()
//            .map(TransactionDTO::getAmount)
//            .reduce(BigDecimal.ZERO, BigDecimal::add)
//            .setScale(2, RoundingMode.HALF_UP);

        //指定分頁裡面，每筆交易經過換算後的本位幣金額的分頁總計：        
        BigDecimal sumConverted = content.stream()
            .map(TransactionDTO::getAmountInBaseCurrency)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(3, RoundingMode.HALF_UP);

        // 6. 計算總頁數 & 是否最後一頁
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean isLast = page + 1 >= totalPages;

        return new PagedResponse<>(
            content,
            page,
            size,
            totalElements,
            totalPages,
            isLast,
//            sumOriginal,
            sumConverted
        );
    }
}
