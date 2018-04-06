package com.noobug.nooblog.service;

import com.noobug.nooblog.consts.UserConst;
import com.noobug.nooblog.consts.error.UserError;
import com.noobug.nooblog.domain.User;
import com.noobug.nooblog.domain.UserRole;
import com.noobug.nooblog.repository.UserRepository;
import com.noobug.nooblog.repository.UserRoleRepository;
import com.noobug.nooblog.tools.entity.Result;
import com.noobug.nooblog.tools.utils.SecurityUtil;
import com.noobug.nooblog.tools.utils.ValidateUtil;
import com.noobug.nooblog.web.dto.UserRegDTO;
import com.noobug.nooblog.web.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserMapper userMapper;


    public Mono<Result> reg(UserRegDTO regDTO) {

        // 注册参数合法性判断
        if (!ValidateUtil.lengthBetween(regDTO.getAccount(), 5, 18)) {
            return Mono.just(Result.error(UserError.REGIST_ACCOUNT_LENGTH));
        } else if (!ValidateUtil.lengthBetween(regDTO.getPassword(), 5, 18)) {
            return Mono.just(Result.error(UserError.REGIST_PASSWORD_LENGTH));
        } else if (!ValidateUtil.lengthBetween(regDTO.getNickName(), 1, 18)) {
            return Mono.just(Result.error(UserError.REGIST_NICKNAME_LENGTH));
        } else if (ValidateUtil.existChinese(regDTO.getAccount())) {
            return Mono.just(Result.error(UserError.REGIST_ACCOUNT_EXIST_CHINESE));
        } else if (ValidateUtil.existSpace(regDTO.getAccount())) {
            return Mono.just(Result.error(UserError.REGIST_ACCOUNT_EXIST_SPACE));
        } else if (ValidateUtil.allNumber(regDTO.getAccount())) {
            return Mono.just(Result.error(UserError.REGIST_ACCOUNT_ALL_NUMBER));
        } else if (userRepository.findByAccountAndDeleted(regDTO.getAccount(), Boolean.FALSE).isPresent()) {
            return Mono.just(Result.error(UserError.REGIST_EXISTED_ACCOUNT));
        } else if (ValidateUtil.existSpace(regDTO.getPassword())) {
            return Mono.just(Result.error(UserError.REGIST_PASSWORD_EXIST_SPACE));
        } else if (ValidateUtil.allSpace(regDTO.getNickName())) {
            return Mono.just(Result.error(UserError.REGIST_NICKNAME_ALL_SPACE));
        } else if (!ValidateUtil.isEmail(regDTO.getEmail())) {
            return Mono.just(Result.error(UserError.REGIST_EMAIL_INVALID));
        }

        // 加密密码
        String md5 = SecurityUtil.md5(regDTO.getPassword());
        regDTO.setPassword(md5);

        User user = userMapper.regDTO2User(regDTO);
        user.setAuthenticated(Boolean.FALSE);
        user.setBanned(Boolean.FALSE);
        user.setDeleted(Boolean.FALSE);
        user.setIsPublic(Boolean.TRUE);
        user.setScore(0);
        user.setSex(UserConst.Sex.UNKNOWN);
        user = userRepository.save(user);

        // 设置为用户角色
        try {
            setDefaultRole(user);


        } catch (Exception e) {
            log.error("[新用户注册] 设置默认角色失败", e);
        }

        log.info("[新用户注册] {}", regDTO);
        return Mono.just(Result.ok());
    }

    /**
     * 设置默认角色
     *
     * @param user 用户实体
     */
    private void setDefaultRole(User user) throws Exception {
        Assert.notNull(user, "[设置默认用户角色] NPE");
        Assert.notNull(user.getId(), "[设置默认用户角色] ID为空");
        roleService.findOneByCode(UserConst.DEFAULT_ROLE)
                .map(role -> {
                    UserRole userRole = new UserRole(null, user.getId(), role.getId());
                    return userRoleRepository.save(userRole);
                })
                .orElseThrow(() -> new Exception("[设置默认用户角色] 数据库无该角色code"));
    }
}
