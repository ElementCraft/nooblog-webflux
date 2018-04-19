package com.noobug.nooblog.repository;

import com.noobug.nooblog.domain.SystemConfig;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {

    List<SystemConfig> getAllByDeleted(Boolean deleted);

    Optional<SystemConfig> findOneByKeyAndDeleted(String key, Boolean isDeleted);

    List<SystemConfig> findAllByDeleted(Boolean isDeleted, Pageable pageable);
}
