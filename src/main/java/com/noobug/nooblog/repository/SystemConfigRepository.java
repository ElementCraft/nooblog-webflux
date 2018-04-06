package com.noobug.nooblog.repository;

import com.noobug.nooblog.domain.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {

    List<SystemConfig> getAllByDeleted(Boolean deleted);

}
