package com.noobug.nooblog.repository;

import com.noobug.nooblog.domain.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLogRepository extends JpaRepository<UserLog, Long> {

}
