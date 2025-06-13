package ebanking.kafka;

import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ebanking.dto.TransactionDTO;
import ebanking.model.TransactionEntity;

@Component
public class TransactionConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TransactionConsumer.class);

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper mapper;

    @Autowired
    public TransactionConsumer(JdbcTemplate jdbcTemplate, ObjectMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @KafkaListener(
	    topics          = "transactions",
	    groupId         = "transaction-api-group",
	    containerFactory= "kafkaListenerContainerFactory"
	)
	public void onMessage(ConsumerRecord<String, String> rec) {
    	
      try {
    	  UUID txId = UUID.fromString(rec.key());
    	  String rawJson = rec.value();
    	  TransactionEntity entity = mapper.readValue(rawJson, TransactionEntity.class);
    	  
//    	  UUID txId = UUID.fromString(entity.getId());
//    	  UUID customerId = UUID.fromString(xxx);
//    	  customerId從table取，用transactio.account_iban關聯account.iban取得customerId，就能取得customer
//    	  P-0123456789
    	  
	  	    // 手動 map 成 DTO
	  	    TransactionDTO dto = new TransactionDTO(
	  	        entity.getId(),
	  	        entity.getAccountIban(),
	  	        //customerId
	  	        null,
	  	        entity.getAmount(),
	  	        entity.getCurrency(),
	  	        entity.getValueDate(),
	  	        entity.getDescription()
	  	    );

	      // 2. Upsert 到 core.transaction
	      jdbcTemplate.update("""
	          INSERT INTO core.transaction (
	            id, account_iban, amount, currency,
	            value_date, description, payload
	          ) VALUES (?,?,?,?,?,?,?::jsonb)
	          ON CONFLICT (id, value_date) DO NOTHING
	          """,
	          txId,
	          dto.getAccountIban(),
	          dto.getAmount(),
	          dto.getCurrency(),
	          dto.getValueDate(),
	          dto.getDescription(),
	          rawJson
	      );
	      logger.info("Upserted transaction {} into DB", dto.getId());
	  } catch (Exception e) {
	      logger.error("Failed to process record {}", rec, e);
	  }    	
    	
	    
	}    
}
