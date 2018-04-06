package com.noobug.nooblog;

import com.noobug.nooblog.domain.SystemConfig;
import com.noobug.nooblog.repository.SystemConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import com.noobug.nooblog.tools.utils.ConfigUtil;

import java.util.List;

/**
 * 初始化
 *
 * @author 小王子
 */
@Slf4j
@Component
public class InitRunner implements ApplicationRunner {

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    /**
     * 初始化读取配置
     */
    private void initConfigUtil(){
        List<SystemConfig> configs = systemConfigRepository.getAllByDeleted(Boolean.FALSE);
        for (SystemConfig config : configs){
            configUtil.add(config.getKey(), config.getData());
        }

        log.info("[初始化配置项] ===== 已完成：共 {} 项 =====", configs.size());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initConfigUtil();
    }
}
