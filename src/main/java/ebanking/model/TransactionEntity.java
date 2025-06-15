package ebanking.model;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction", schema = "core" , indexes = {
        @Index(name = "idx_account_value_date", columnList = "account_iban, value_date")
})
public class TransactionEntity {

	 @Id
	  @GeneratedValue(generator = "UUID")
	  @GenericGenerator(
	    name = "UUID",
	    strategy = "org.hibernate.id.UUIDGenerator"
	  )
	  @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
	  private UUID id;

	 @Column(name = "account_iban",
		        columnDefinition = "char(34)",
		        nullable = false)
	 @JdbcTypeCode(Types.CHAR)
    private String accountIban;

    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "value_date", nullable = false)
    private LocalDate valueDate;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public TransactionEntity() {
        // JPA
    }

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    // -------- Getters & Setters --------


    public String getAccountIban() {
        return accountIban;
    }

    public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public void setAccountIban(String accountIban) {
        this.accountIban = accountIban;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
