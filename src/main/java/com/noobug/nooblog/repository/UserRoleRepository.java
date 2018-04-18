package com.noobug.nooblog.repository;

import com.noobug.nooblog.domain.User;
import com.noobug.nooblog.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findAllByUserId(Long userId);
}
