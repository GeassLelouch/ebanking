```mermaid
sequenceDiagram
    %% API 查詢流程
    actor User
    participant Auth      as AuthController
    participant JWT       as JWT Token
    participant Filter    as JwtAuthenticationFilter
    participant Controller as TransactionController
    participant Service   as TransactionService
    participant Repo      as TransactionRepository
    participant DB        as PostgreSQL
    participant RateSvc   as ExchangeRateService
    participant RateAPI   as External Rate API
    participant Mapper    as TransactionMapper
    participant Pager     as PagedResponse

    %% 登入並領取 Token
    User ->> Auth : login(credentials)
    Auth -->> JWT  : token

    %% 用 Token 呼叫查交易
    User ->> Filter     : GET /api/transactions + token
    Filter ->> Controller: forward request

    Controller ->> Service : getTransactions(params)
    
    %% 撈交易資料
    Service ->> Repo    : findByAccountAndMonth(...)
    Repo    ->> DB      : SELECT * FROM transaction
    DB      -->> Repo   : rows
    Repo    -->> Service: entities

    %% 拿匯率做換算
    Service ->> RateSvc : getRate(from, to)
    RateSvc ->> RateAPI : HTTP GET /rate
    RateAPI-->> RateSvc : rate
    RateSvc-->> Service : rate

    %% DTO 轉換與分頁
    Service ->> Mapper : toDTO(entities, rate)
    Mapper  -->> Service: DTO list

    Service ->> Pager   : buildPage(DTO list, pagination)
    Pager   -->> User    : paged result

```



# e-Banking Transaction Service (PostgreSQL Edition)

本專案實現一個微服務 (Java 17 + Spring Boot 3)，主要功能：
- 從 Kafka 消費交易訊息並儲存到 PostgreSQL
- 按「帳戶 IBAN + 年月」分頁查詢交易清單
- 查詢結果中，對每筆交易做匯率換算並回傳當頁總借總貸
- 採用 JWT 驗證，僅允許帶 Bearer Token 之使用者查詢自己的帳號
- 整合 Spring Boot Actuator + Micrometer，暴露 Prometheus 指標
- 日誌使用 Logback，顯示 SQL、Kafka 訊息等
- 單元 / 整合測試 (JUnit 5 + Mockito + Testcontainers)
- Docker 化，提供 Dockerfile
- Kubernetes 部署範例 (ConfigMap、Deployment、Service)
- CircleCI 配置範例，自動化測試與映像推送

---

## 目錄結構
```text
e-banking-transaction-service/
├── .circleci/ # CircleCI 設定
├── k8s/ # Kubernetes 部署檔
├── Dockerfile # Docker 映像建置
├── README.md # 本檔案
├── pom.xml # Maven 設定
└── src/
├── main/
│ ├── java/ebanking/
│ │ ├── TransactionServiceApplication.java
│ │ ├── config/ # KafkaConfig, SecurityConfig, OpenAPIConfig
│ │ ├── controller/ # TransactionController.java
│ │ ├── dto/ # TransactionDTO, PagedResponse
│ │ ├── exception/ # ResourceNotFoundException, ApiExceptionHandler
│ │ ├── model/ # TransactionEntity
│ │ ├── repository/ # TransactionRepository
│ │ ├── security/ # JwtUtil, JwtAuthenticationFilter, UserDetailsServiceImpl
│ │ ├── service/ # ExchangeRateService, KafkaConsumerService, TransactionService
│ │ └── util/ # ModelMapperConfig
│ └── resources/
│ ├── application.yml
│ └── logback-spring.xml
└── test/
└── java/ebanking/
├── controller/ # TransactionControllerIntegrationTest
├── repository/ # TransactionRepositoryTest
└── service/ # TransactionServiceTest
```

---

## 本地開發

### 環境需求

- JDK 17
- Maven 3.8+
- Docker (用於 Testcontainers 或本地測試)
- PostgreSQL (若不使用 Testcontainers)
- Kafka (若不使用 Testcontainers)

### 設定環境變數 (可選)

- `POSTGRES_HOST` (default: `localhost`)
- `POSTGRES_PORT` (default: `5432`)
- `POSTGRES_USER` (default: `postgres`)
- `POSTGRES_PASSWORD` (default: `postgres`)
- `KAFKA_BOOTSTRAP_SERVERS` (default: `localhost:9092`)
- `JWT_SECRET` (務必自行設置隨機字串)

### 執行步驟

1. `git clone <repository_url>`
2. 開啟 IDE，匯入 Maven 專案
3. 在 `application.yml` 裡確認資料庫與 Kafka 設定
4. 執行 `ebanking.TransactionServiceApplication.main(...)`
5. 開啟瀏覽器查看 Swagger UI：  

http://localhost:8080/swagger-ui.html

6. 取得 JWT：  
- 本範例僅提供硬編碼的測試使用者 `user123/userpass`，可自行在 `UserDetailsServiceImpl` 修改
- 可使用 `JwtUtil` 類產生 Token：  
  ```java
  String token = jwtUtil.generateToken("CH93000000000000000000");
  ```
- 帶上 Header：  
  ```
  Authorization: Bearer <token>
  ```

### 執行測試

```bash
mvn clean test

單元測試：TransactionServiceTest

Repository 測試：TransactionRepositoryTest (Testcontainers PostgreSQL)

整合測試：TransactionControllerIntegrationTest (Testcontainers PostgreSQL + Kafka)






# Kafka 消费时序图

```mermaid
sequenceDiagram
    participant KafkaTopic   as Kafka Topic
    participant KafkaConsumer as KafkaConsumerService
    participant Repo          as TransactionRepository
    participant DB            as PostgreSQL

    KafkaTopic   ->> KafkaConsumer : onMessage(transactionEntity)
    KafkaConsumer->> Repo         : save(entity)
    Repo         ->> DB           : INSERT transaction
    DB           -->> Repo        : OK
    Repo         -->> KafkaConsumer: saved

```
