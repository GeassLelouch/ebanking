//package ebanking.service;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.UUID;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.modelmapper.ModelMapper;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import ebanking.dto.PagedResponse;
//import ebanking.dto.TransactionDTO;
//import ebanking.exception.ResourceNotFoundException;
//import ebanking.model.TransactionEntity;
//import ebanking.repository.TransactionRepository;
//
//class TransactionServiceTest {
//
//    private TransactionRepository transactionRepository;
//    private ExchangeRateService exchangeRateService;
//    private ModelMapper modelMapper;
//    private TransactionService transactionService;
//
//    @BeforeEach
//    void setUp() {
//        transactionRepository = mock(TransactionRepository.class);
//        exchangeRateService = mock(ExchangeRateService.class);
//        modelMapper = new ModelMapper();
//
//        transactionService = new TransactionService(
//            transactionRepository,
//            modelMapper,
//            exchangeRateService
//        );
//    }
//
//
//    @Test
//    void getTransactionsByMonth_noResults_shouldThrow() {
//        String accountIban = "CH93000000000000000000";
//        int year = 2022, month = 10, page = 0, size = 10;
//
//        when(transactionRepository.findByAccountIbanAndValueDateBetween(
//                eq(accountIban),
//                any(LocalDate.class),
//                any(LocalDate.class),
//                any(Pageable.class)))
//                .thenReturn(Page.empty());
//
//        assertThatThrownBy(() ->
//                transactionService.getTransactionsByMonth(accountIban, year, month, page, size))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessageContaining("No transactions found for account");
//    }
//
//    @Test
//    void getTransactionsByMonth_withResults_shouldReturnPagedAndSum() {
//        String accountIban = "CH93000000000000000000";
//        int year = 2022, month = 10, page = 0, size = 10;
//
//        TransactionEntity tx1 = new TransactionEntity();
//        tx1.setId(UUID.randomUUID());
//        tx1.setAccountIban(accountIban);
//        tx1.setAmount(BigDecimal.valueOf(100));
//        tx1.setCurrency("EUR");
//        tx1.setValueDate(LocalDate.of(2022, 10, 5));
//        tx1.setDescription("Test1");
//
//        TransactionEntity tx2 = new TransactionEntity();
//        tx2.setId(UUID.randomUUID());
//        tx2.setAccountIban(accountIban);
//        tx2.setAmount(BigDecimal.valueOf(50));
//        tx2.setCurrency("EUR");
//        tx2.setValueDate(LocalDate.of(2022, 10, 10));
//        tx2.setDescription("Test2");
//
//        List<TransactionEntity> content = List.of(tx1, tx2);
//        Page<TransactionEntity> pageResult = new PageImpl<>(content,
//                PageRequest.of(page, size), content.size());
//
//        when(transactionRepository.findByAccountIbanAndValueDateBetween(
//                eq(accountIban),
//                any(LocalDate.class),
//                any(LocalDate.class),
//                any(Pageable.class)))
//                .thenReturn(pageResult);
//
//        when(exchangeRateService.getRateToBase("EUR"))
//                .thenReturn(BigDecimal.valueOf(1.1));
//
//        PagedResponse<TransactionDTO> response =
//                transactionService.getTransactionsByMonth(accountIban, year, month, page, size);
//
//        assertThat(response.getContent()).hasSize(2);
//        assertThat(response.getSumAmount()).isEqualTo(new BigDecimal("150.0"));
////        assertThat(response.getSumAmountInBaseCurrency()).isEqualTo(new BigDecimal("165.0"));
//
//        TransactionDTO dto1 = response.getContent().get(0);
//        assertThat(dto1.getId()).isEqualTo("id-001");
////        assertThat(dto1.getAmountInBaseCurrency()).isEqualByComparingTo(BigDecimal.valueOf(110.0));
//    }
//}
