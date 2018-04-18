package com.noobug.nooblog.repository;

import com.noobug.nooblog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByAccountAndDeleted(String account, Boolean isDeleted);

    Optional<User> findByIdAndDeleted(Long id, Boolean isDeleted);

    Optional<User> findByAccountAndPasswordAndBannedAndDeleted(String account, String password, Boolean isBanned, Boolean isDeleted);
}
