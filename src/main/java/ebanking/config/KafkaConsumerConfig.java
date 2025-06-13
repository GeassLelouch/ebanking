//package ebanking.config;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//
//import ebanking.model.TransactionEntity;
//
//@Configuration
//public class KafkaConsumerConfig {
//
//    @Bean
//    public Map<String, Object> consumerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//        // 基本 Kafka 設定
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "transaction-api-group");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//        // 信任你的 model package
//        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ebanking.model");
//        // 指定預設解序列化成這個 class
//        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, TransactionEntity.class);
//        return props;
//    }
//
//    @Bean
//    public ConsumerFactory<String, TransactionEntity> consumerFactory() {
//        // String key + JSON value -> TransactionEntity
//        JsonDeserializer<TransactionEntity> deserializer = 
//            new JsonDeserializer<>(TransactionEntity.class);
//        deserializer.addTrustedPackages("ebanking.model");
//        return new DefaultKafkaConsumerFactory<>(
//            consumerConfigs(),
//            new StringDeserializer(),
//            deserializer
//        );
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, TransactionEntity>
//        kafkaListenerContainerFactory() {
//
//        ConcurrentKafkaListenerContainerFactory<String, TransactionEntity> factory =
//            new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//
//        // 若需要可以加 error handler、retry policy 之類的
//        return factory;
//    }
//}
