package ebanking.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ebanking.model.UserEntity;

//2. 建立 Repository
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
 /**
  * 根據 username 查使用者
  */
 Optional<UserEntity> findByUsername(String username);
}
