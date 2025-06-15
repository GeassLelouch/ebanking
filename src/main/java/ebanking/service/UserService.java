// src/main/java/ebanking/service/TransactionService.java
package ebanking.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ebanking.dto.PagedResponse;
import ebanking.dto.TransactionDTO;
import ebanking.exception.ResourceNotFoundException;
import ebanking.model.TransactionEntity;
import ebanking.model.UserEntity;
import ebanking.repository.TransactionRepository;

//3. 定義 Service 介面
public interface UserService {
 /**
  * 從 username 取得對應的 customerId，
  * 如果找不到就丟出 UsernameNotFoundException
  */
 String findCustomerIdByUsername(String username);
 
 UserEntity loadByUsername(String username);
}

