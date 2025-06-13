//package ebanking.repository;
//
//import ebanking.model.TransactionEntity;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.*;
//
////@Testcontainers
//@DataJpaTest
//@Import(TransactionRepository.class)
//class TransactionRepositoryTest {
//
//    @Container
//    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine")
//            .withDatabaseName("testdb")
//            .withUsername("testuser")
//            .withPassword("testpass");
//
//    @DynamicPropertySource
//    static void registerDynamicProps(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgres::getJdbcUrl);
//        registry.add("spring.datasource.username", postgres::getUsername);
//        registry.add("spring.datasource.password", postgres::getPassword);
//    }
//
//    @Autowired
//    private TransactionRepository repository;
//
//    private TransactionEntity tx1, tx2, tx3;
//
//    @BeforeEach
//    void setup() {
//        repository.deleteAll();
//
//        tx1 = new TransactionEntity();
//        tx1.setId(UUID.randomUUID().toString());
//        tx1.setAccountIban("CH93000000000000000000");
//        tx1.setAmount(BigDecimal.valueOf(100));
//        tx1.setCurrency("EUR");
//        tx1.setValueDate(LocalDate.of(2022, 10, 5));
//        tx1.setDescription("Desc1");
//
//        tx2 = new TransactionEntity();
//        tx2.setId(UUID.randomUUID().toString());
//        tx2.setAccountIban("CH93000000000000000000");
//        tx2.setAmount(BigDecimal.valueOf(50));
//        tx2.setCurrency("EUR");
//        tx2.setValueDate(LocalDate.of(2022, 10, 15));
//        tx2.setDescription("Desc2");
//
//        tx3 = new TransactionEntity();
//        tx3.setId(UUID.randomUUID().toString());
//        tx3.setAccountIban("CH93000000000000000000");
//        tx3.setAmount(BigDecimal.valueOf(200));
//        tx3.setCurrency("USD");
//        tx3.setValueDate(LocalDate.of(2022, 11, 1));
//        tx3.setDescription("Desc3");
//
//        repository.save(tx1);
//        repository.save(tx2);
//        repository.save(tx3);
//    }
//
//    @Test
//    void testFindByAccountIbanAndValueDateBetween() {
//        String iban = "CH93000000000000000000";
//        LocalDate start = LocalDate.of(2022, 10, 1);
//        LocalDate end = LocalDate.of(2022, 10, 31);
//
//        Page<TransactionEntity> page = repository.findByAccountIbanAndValueDateBetween(
//                iban, start, end,
//                PageRequest.of(0, 10)
//        );
//
//        assertThat(page.getTotalElements()).isEqualTo(2);
//        assertThat(page.getContent())
//                .extracting("id")
//                .containsExactlyInAnyOrder(tx1.getId(), tx2.getId());
//    }
//}
