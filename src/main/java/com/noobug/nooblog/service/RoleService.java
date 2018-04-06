package com.noobug.nooblog.service;

import com.noobug.nooblog.domain.Role;
import com.noobug.nooblog.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * 通过CODE查找角色实体
     *
     * @param code code
     * @return
     */
    public Optional<Role> findOneByCode(String code) {
        return roleRepository.findOneByCodeAndDeleted(code, Boolean.FALSE);
    }
}
