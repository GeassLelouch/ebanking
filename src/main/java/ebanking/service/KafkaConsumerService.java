package ebanking.service;

import ebanking.model.TransactionEntity;
import ebanking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @KafkaListener(topics = "${transaction.topic.name:transactions-topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(TransactionEntity transaction) {
        try {
            logger.info("Consume transaction: {}", transaction.getId());
            transactionRepository.save(transaction);
        } catch (Exception ex) {
            logger.error("Error saving transaction {}: {}", transaction.getId(), ex.getMessage());
        }
    }
}
