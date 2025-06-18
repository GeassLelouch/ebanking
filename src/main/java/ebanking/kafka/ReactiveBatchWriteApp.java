package ebanking.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.r2dbc.core.DatabaseClient;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;

import ebanking.dto.TransactionDTO;

@Slf4j
@Configuration
public class ReactiveBatchWriteApp {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveBatchWriteApp.class, args);
    }

    // 1) 建立 ReceiverOptions<String, TransactionDTO>
    @Bean
    public ReceiverOptions<String, TransactionDTO> kafkaReceiverOptions(
            @Value("${spring.kafka.bootstrap-servers}") String brokers) {

        Map<String, Object> props = Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
            JsonDeserializer.VALUE_DEFAULT_TYPE, TransactionDTO.class,
            JsonDeserializer.TRUSTED_PACKAGES, "*",
            ConsumerConfig.GROUP_ID_CONFIG, "tx-group"
        );

        return ReceiverOptions.<String, TransactionDTO>create(props)
                .subscription(List.of("transactions-topic"));
    }

    // 2) 建立唯一的 KafkaReceiver bean
    @Bean
    public KafkaReceiver<String, TransactionDTO> kafkaReceiver(
            ReceiverOptions<String, TransactionDTO> opts) {
        return KafkaReceiver.create(opts);
    }

    // 3) 啟動反應式批次寫入
    @Bean
    public Disposable inboundPipeline(KafkaReceiver<String, TransactionDTO> receiver,
                                      DatabaseClient dbClient) {

        int batchSize = 500;
        Duration maxDelay = Duration.ofMillis(500);

        return receiver.receive() // Flux<ReceiverRecord<String,TransactionDTO>>
            .map(ReceiverRecord::value)                         // Flux<TransactionDTO>
            .bufferTimeout(batchSize, maxDelay)                 // 批次
            .filter(batch -> !batch.isEmpty())
            .flatMap(batch -> batchInsert(dbClient, batch))     // 寫入 DB
            .subscribe(
                count -> log.info("已寫入 batch, 總筆數 = {}", count),
                err   -> log.error("寫入失敗", err)
            );
    }

    // 4) 批次 UNNEST 寫入
    private Mono<Long> batchInsert(DatabaseClient client, List<TransactionDTO> batch) {
        List<UUID>       ids        = batch.stream()
                                           .map(tx -> UUID.fromString(tx.getId()))
                                           .toList();
        List<BigDecimal> amounts    = batch.stream()
                                           .map(TransactionDTO::getAmount)
                                           .toList();
        List<String>     currencies = batch.stream()
                                           .map(TransactionDTO::getCurrency)
                                           .toList();
        List<LocalDate>  dates      = batch.stream()
                                           .map(TransactionDTO::getValueDate)
                                           .toList();
        List<String>     descs      = batch.stream()
                                           .map(TransactionDTO::getDescription)
                                           .toList();

        String sql = """
            INSERT INTO transactions
              (id, amount, currency, value_date, description)
            SELECT UNNEST($1::uuid[]),
                   UNNEST($2::numeric[]),
                   UNNEST($3::text[]),
                   UNNEST($4::date[]),
                   UNNEST($5::text[]);
            """;

        return client.sql(sql)
            .bind("$1", ids)
            .bind("$2", amounts)
            .bind("$3", currencies)
            .bind("$4", dates)
            .bind("$5", descs)
            .fetch()
            .rowsUpdated();
    }
}
