// src/main/java/ebanking/service/TransactionService.java
package ebanking.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ebanking.dto.PagedResponse;
import ebanking.dto.TransactionDTO;
import ebanking.exception.ResourceNotFoundException;
import ebanking.model.TransactionEntity;
import ebanking.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    private final ExchangeRateService exchangeRateService;
    
    public TransactionService(TransactionRepository transactionRepository,
            ModelMapper modelMapper,
            ExchangeRateService exchangeRateService) {
		this.transactionRepository = transactionRepository;
		this.modelMapper = modelMapper;
		this.exchangeRateService = exchangeRateService;
	}

    public PagedResponse<TransactionDTO> getTransactionsByMonth(
            String accountIban,
            int year,
            int month,
            int page,
            int size
    ) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate   = ym.atEndOfMonth();

        Pageable pageable = PageRequest.of(page, size, Sort.by("valueDate").descending());
        Page<TransactionEntity> txPage = transactionRepository
            .findByAccountIbanAndValueDateBetween(accountIban, startDate, endDate, pageable);

        if (txPage.isEmpty()) {
            throw new ResourceNotFoundException(
                String.format("No transactions found for account %s in %04d-%02d", accountIban, year, month));
        }

        // 1. 先 map entity -> dto
        List<TransactionDTO> dtos = txPage.getContent().stream()
            .map(entity -> modelMapper.map(entity, TransactionDTO.class))
            .collect(Collectors.toList());

//        // 2. 針對每筆 dto 呼叫第三方匯率，再填入 amountInBaseCurrency
//        dtos.forEach(dto -> {
//            BigDecimal rate = exchangeRateService.getRate(dto.getCurrency(), "USD");
//            dto.setAmountInBaseCurrency(dto.getAmount().multiply(rate));
//        });
        
        dtos.forEach(dto -> {
            BigDecimal amount = Objects.requireNonNullElse(dto.getAmount(), BigDecimal.ZERO);
            BigDecimal rate   = Objects.requireNonNullElse(
                exchangeRateService.getRate(dto.getCurrency(), "USD"),
                BigDecimal.ZERO
            );
            dto.setAmountInBaseCurrency(amount.multiply(rate));
        });

        // 3. （可選）計算分頁的貸／借小計
        BigDecimal sumOriginal =
            dtos.stream()
                .map(TransactionDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(1, RoundingMode.UNNECESSARY);;

        BigDecimal sumConverted =
            dtos.stream()
                .map(TransactionDTO::getAmountInBaseCurrency)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(1, RoundingMode.UNNECESSARY);;

        return new PagedResponse<TransactionDTO>(
            dtos,
            txPage.getNumber(),
            txPage.getSize(),
            txPage.getTotalElements(),
            txPage.getTotalPages(),
            txPage.isLast(),
            sumOriginal,
            sumConverted
        );
    }
}
