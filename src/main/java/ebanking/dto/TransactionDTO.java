package ebanking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDTO {
    private String id;
    private String accountIban;
    private String customerId;
    private BigDecimal amount;
    private String currency;
    private LocalDate valueDate;
    private String description;
    
    private BigDecimal amountInBaseCurrency;

    public TransactionDTO() { }

    public TransactionDTO(String id, String accountIban, String customerId,
                          BigDecimal amount, String currency,
                          LocalDate valueDate, String description) {
        this.id = id;
        this.accountIban = accountIban;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.valueDate = valueDate;
        this.description = description;
    }
    
    // --- getters & setters ---

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccountIban() {
		return accountIban;
	}

	public void setAccountIban(String accountIban) {
		this.accountIban = accountIban;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
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

	public BigDecimal getAmountInBaseCurrency() {
		return amountInBaseCurrency;
	}

	public void setAmountInBaseCurrency(BigDecimal amountInBaseCurrency) {
		this.amountInBaseCurrency = amountInBaseCurrency;
	}


   
    
    
}
