package ebanking.repository;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ebanking.model.TransactionEntity;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    // 查指定客戶、再指定帳戶 IBAN 的交易
    Page<TransactionEntity> findByAccountIbanAndValueDateBetween(
        String accountIban,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );

    // 查指定客戶、所有帳戶的交易
    Page<TransactionEntity> findByValueDateBetween(
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );
}
