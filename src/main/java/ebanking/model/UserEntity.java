package ebanking.model;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//1. 定義你的 Entity，假設已經有 customerId 欄位
@Entity
@Table(name = "users", schema = "core")
public class UserEntity {
	
 @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
    name = "UUID",
    strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
  private UUID id;

 @Column(nullable = false, unique = true)
 private String username;

 @Column(name = "password_hash", nullable = false)
 private String passwordHash;

 @Column(name = "customer_id", nullable = false, unique = true)
 private String customerId;



public UUID getId() {
	return id;
}

public void setId(UUID id) {
	this.id = id;
}

public String getUsername() {
	return username;
}

public void setUsername(String username) {
	this.username = username;
}

public String getPasswordHash() {
	return passwordHash;
}

public void setPasswordHash(String passwordHash) {
	this.passwordHash = passwordHash;
}

public String getCustomerId() {
	return customerId;
}

public void setCustomerId(String customerId) {
	this.customerId = customerId;
}

}
