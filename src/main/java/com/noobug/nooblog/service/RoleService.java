package com.noobug.nooblog.service;

import com.noobug.nooblog.domain.Role;
import com.noobug.nooblog.domain.UserRole;
import com.noobug.nooblog.repository.AuthorityRepository;
import com.noobug.nooblog.repository.RoleAuthorityRepository;
import com.noobug.nooblog.repository.RoleRepository;
import com.noobug.nooblog.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private RoleAuthorityRepository roleAuthorityRepository;

    /**
     * 通过CODE查找角色实体
     *
     * @param code code
     * @return
     */
    public Optional<Role> findOneByCode(String code) {
        return roleRepository.findOneByCodeAndDeleted(code, Boolean.FALSE);
    }

    /**
     * 找出用户所有角色
     *
     * @param userId 用户ID
     * @return
     */
    public List<Role> findAllByUserId(Long userId) {
        List<UserRole> userRoles = userRoleRepository.findAllByUserId(userId);

        return userRoles.stream()
                .map(userRole -> roleRepository.findById(userRole.getRoleId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
