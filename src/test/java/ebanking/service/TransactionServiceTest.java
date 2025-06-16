package ebanking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import ebanking.dto.PagedResponse;
import ebanking.dto.TransactionDTO;
import ebanking.mapper.TransactionMapper;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
	
	@Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void registerDynamicProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private TransactionMapper transactionMapper;
    private ExchangeRateService exchangeRateService;
    private UserService userService;
    private ModelMapper modelMapper;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionMapper    = mock(TransactionMapper.class);
        exchangeRateService  = mock(ExchangeRateService.class);
        userService          = mock(UserService.class);
        modelMapper          = new ModelMapper();

        transactionService = new TransactionService(
            transactionMapper,
            modelMapper,
            exchangeRateService,
            userService
        );
    }

    @Test
    void getTransactionsByMonth_noResults_shouldReturnEmptyPage() {
        String customerId = "cust-1";
        int year = 2022, month = 10, page = 0, size = 10;

        // stub mapper to return no data
        when(transactionMapper.findByCustomerAndMonth(anyMap()))
            .thenReturn(Collections.emptyList());
        when(transactionMapper.countByCustomerAndMonth(anyMap()))
            .thenReturn(0);

        // call service
        PagedResponse<TransactionDTO> resp =
            transactionService.getTransactionsByMonth(
                customerId, year, month, page, size, null);

        // verify empty paging
        assertThat(resp.getContent()).isEmpty();
        assertThat(resp.getTotalElements()).isZero();
        assertThat(resp.getTotalPages()).isZero();
        assertThat(resp.isLast()).isTrue();
        // sumConverted 預設為 0，scale=3
        assertThat(resp.getSumAmountInBaseCurrency())
            .isEqualByComparingTo(BigDecimal.ZERO.setScale(3));
    }

    @Test
    void getTransactionsByMonth_withResults_shouldReturnPagedAndSum() {
        String customerId = "cust-1";
        int year = 2022, month = 10, page = 0, size = 10;
        String iban = "CH93000000000000000000";

        // prepare DTOs
        TransactionDTO dto1 = new TransactionDTO();
        dto1.setId("id-001");
        dto1.setCurrency("EUR");
        dto1.setAmount(BigDecimal.valueOf(100));

        TransactionDTO dto2 = new TransactionDTO();
        dto2.setId("id-002");
        dto2.setCurrency("EUR");
        dto2.setAmount(BigDecimal.valueOf(50));

        List<TransactionDTO> dtos = List.of(dto1, dto2);

        // stub mapper
        when(transactionMapper.findByCustomerAndMonth(anyMap()))
            .thenReturn(dtos);
        when(transactionMapper.countByCustomerAndMonth(anyMap()))
            .thenReturn(dtos.size());
        // stub exchange rate: 1 EUR = 1.1 base
        when(exchangeRateService.getRate(eq("EUR"),
                eq(ExchangeRateService.BASE_CURRENCY)))
            .thenReturn(BigDecimal.valueOf(1.1));

        // call service
        PagedResponse<TransactionDTO> resp =
            transactionService.getTransactionsByMonth(
                customerId, year, month, page, size, iban);

        // verify content and sums
        assertThat(resp.getContent()).hasSize(2);

        // sumConverted = (100 + 50) * 1.1 = 165.000 (scale=3)
        assertThat(resp.getSumAmountInBaseCurrency())
            .isEqualByComparingTo(new BigDecimal("165.000"));

        // check first DTO conversion
        TransactionDTO result1 = resp.getContent().get(0);
        assertThat(result1.getId()).isEqualTo("id-001");
        assertThat(result1.getAmountInBaseCurrency())
            .isEqualByComparingTo(new BigDecimal("110.0"));
    }
}
