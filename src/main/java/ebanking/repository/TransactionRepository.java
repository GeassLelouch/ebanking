package ebanking.repository;

import ebanking.model.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {

    /**
     * 根據帳戶 IBAN，以及起息日區間，分頁查詢交易
     */
    Page<TransactionEntity> findByAccountIbanAndValueDateBetween(
            String accountIban,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
}
