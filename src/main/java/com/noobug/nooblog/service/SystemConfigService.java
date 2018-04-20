package com.noobug.nooblog.service;

import com.noobug.nooblog.consts.SystemConfigConst;
import com.noobug.nooblog.consts.error.SystemConfigError;
import com.noobug.nooblog.domain.SystemConfig;
import com.noobug.nooblog.repository.SystemConfigRepository;
import com.noobug.nooblog.tools.entity.Result;
import com.noobug.nooblog.tools.utils.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * 系统配置表逻辑相关
 *
 * @author 小王子
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SystemConfigService {

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    /**
     * 新增数据库配置项
     *
     * @param systemConfig 配置项实体
     * @return 结果
     */
    public Mono<Result<Object>> add(SystemConfig systemConfig) {
        String key = systemConfig.getKey();
        String data = systemConfig.getData();

        // 验证参数有效性
        if (!ValidateUtil.lengthBetween(key, SystemConfigConst.Limit.LEN_KEY_MIN, SystemConfigConst.Limit.LEN_KEY_MAX)) {
            return Mono.just(Result.error(SystemConfigError.KEY_LENGTH));
        } else if (ValidateUtil.existSpace(key)) {
            return Mono.just(Result.error(SystemConfigError.KEY_CONTAIN_SPACE));
        } else if (ValidateUtil.existChinese(key)) {
            return Mono.just(Result.error(SystemConfigError.KEY_CONTAIN_CHINESE));
        } else if (systemConfigRepository.findOneByKeyAndDeleted(key, Boolean.FALSE).isPresent()) {
            return Mono.just(Result.error(SystemConfigError.EXIST_KEY));
        } else if (!ValidateUtil.lengthBetween(data, SystemConfigConst.Limit.LEN_DATA_MIN, SystemConfigConst.Limit.LEN_DATA_MAX)) {
            return Mono.just(Result.error(SystemConfigError.DATA_LENGTH));
        }

        systemConfig.setDeleted(Boolean.FALSE);
        systemConfigRepository.save(systemConfig);
        return Mono.just(Result.ok());
    }

    /**
     * 获取数据库配置项分页
     *
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Transactional(readOnly = true)
    public Mono<Page<SystemConfig>> getAllByPage(Pageable pageable) {
        Page<SystemConfig> configs = systemConfigRepository.findAllByDeleted(Boolean.FALSE, pageable);
        return Mono.just(configs);
    }

    /**
     * 编辑数据库配置项
     *
     * @param systemConfig 配置项实体
     * @return 结果
     */
    public Mono<Result<Object>> edit(SystemConfig systemConfig) {
        Optional<SystemConfig> dbConfig = systemConfigRepository.findById(systemConfig.getId());
        String data = systemConfig.getData();

        return dbConfig.map(o -> {

            // data长度验证
            if (!ValidateUtil.lengthBetween(data, SystemConfigConst.Limit.LEN_DATA_MIN, SystemConfigConst.Limit.LEN_DATA_MAX)) {
                return Mono.just(Result.error(SystemConfigError.DATA_LENGTH));
            }

            // 更新数据库
            o.setData(data);
            systemConfigRepository.save(o);

            return Mono.just(Result.ok());
        }).orElse(Mono.just(Result.error(SystemConfigError.NON_EXIST_ID)));
    }

    /**
     * 通过ID删除
     *
     * @param id 配置项ID
     * @return 结果
     */
    public Mono<Result<Object>> delById(Long id) {
        return systemConfigRepository.findById(id)
                .map(o -> {
                    if (!o.getDeleted()) {
                        o.setDeleted(Boolean.TRUE);
                        systemConfigRepository.save(o);
                    }

                    return Mono.just(Result.ok());
                }).orElse(Mono.just(Result.error(SystemConfigError.NON_EXIST_ID)));
    }

    // TODO 重新加载配置项的逻辑
    @Transactional(readOnly = true)
    public Mono<Void> reload() {
        return Mono.empty();
    }
}
