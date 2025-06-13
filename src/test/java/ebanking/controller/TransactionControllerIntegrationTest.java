//package ebanking.controller;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.Map;
//import java.util.UUID;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.KafkaContainer;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.utility.DockerImageName;
//
//import ebanking.model.TransactionEntity;
//import ebanking.repository.TransactionRepository;
//import ebanking.security.JwtUtil;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class TransactionControllerIntegrationTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TransactionRepository repository;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine")
//            .withDatabaseName("testdb")
//            .withUsername("testuser")
//            .withPassword("testpass");
//
//    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));
//
//    @DynamicPropertySource
//    static void registerProps(DynamicPropertyRegistry registry) {
//        postgres.start();
//        kafka.start();
//
//        registry.add("spring.datasource.url", postgres::getJdbcUrl);
//        registry.add("spring.datasource.username", postgres::getUsername);
//        registry.add("spring.datasource.password", postgres::getPassword);
//
//        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
//        registry.add("transaction.topic.name", () -> "test-transactions-topic");
//    }
//
//    @BeforeEach
//    void setup() {
//        repository.deleteAll();
//    }
//
//    @Test
//    void testEndToEndFlow() {
//        TransactionEntity tx = new TransactionEntity();
//        tx.setId(UUID.randomUUID().toString());
//        tx.setAccountIban("CH93000000000000000000");
//        tx.setAmount(BigDecimal.valueOf(120));
//        tx.setCurrency("EUR");
//        tx.setValueDate(LocalDate.of(2022, 10, 20));
//        tx.setDescription("IntegrationTest");
//        repository.save(tx);
//
//        String token = jwtUtil.generateToken("CH93000000000000000000");
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + token);
//        HttpEntity<Void> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<Map> response = restTemplate.exchange(
//                "http://localhost:" + port + "/api/transactions?year=2022&month=10&page=0&size=10",
//                HttpMethod.GET,
//                entity,
//                Map.class
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        Map body = response.getBody();
//        assertThat(body).isNotNull();
//        assertThat(body.get("totalElements")).isEqualTo(1);
//        var content = (java.util.ArrayList<Map>) body.get("content");
//        assertThat(content).hasSize(1);
//        assertThat(content.get(0).get("id")).isEqualTo(tx.getId());
//    }
//}
