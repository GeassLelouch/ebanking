// src/main/java/ebanking/service/TransactionService.java
package ebanking.service;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ebanking.model.UserEntity;
import ebanking.repository.UserRepository;

//4. 實作 Service
@Service
public class UserServiceImpl implements UserService {

 private final UserRepository userRepository;
 
//手動建構子注入
 @Autowired
 public UserServiceImpl(UserRepository userRepository) {
     this.userRepository = userRepository;
 }


	 @Override
	 @Transactional(readOnly = true)
	 public String findCustomerIdByUsername(String username) {
	     return userRepository.findByUsername(username)
	         .map(UserEntity::getCustomerId)
	         .orElseThrow(() ->
	             new UsernameNotFoundException("找不到使用者：" + username)
	         );
	 }
	 
	 /** 依 username 找到整個 UserEntity */
	 public UserEntity loadByUsername(String username) {
	     return userRepository
	             .findByUsername(username)
	             .orElseThrow(() ->
	                new NoSuchElementException("User not found: " + username)
	             );
	 }
}

