package com.noobug.nooblog.repository;

import com.noobug.nooblog.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findOneByCodeAndDeleted(String code, Boolean deleted);
}
