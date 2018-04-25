package com.noobug.nooblog.service;

import com.noobug.nooblog.repository.UserLogRepository;
import com.noobug.nooblog.web.dto.UserLogInfoDTO;
import com.noobug.nooblog.web.mapper.UserLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AdminService {

    @Autowired
    private UserLogRepository userLogRepository;

    @Autowired
    private UserLogMapper userLogMapper;

    /**
     * 获取全部用户登录日志分页
     *
     * @param pageable 分页参数
     * @return
     */
    public Mono<Page<UserLogInfoDTO>> getUserLogsPage(Pageable pageable) {
        return Mono.just(userLogRepository.findAll(pageable)
                .map(userLogMapper::toDto));
    }
}
