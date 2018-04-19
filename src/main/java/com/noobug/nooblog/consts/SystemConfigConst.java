package com.noobug.nooblog.consts;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 数据库配置项常量
 *
 * @author noobug.com
 */
public interface SystemConfigConst {

    interface Limit {
        int LEN_KEY_MIN = 1;
        int LEN_KEY_MAX = 64;
        int LEN_DATA_MIN = 1;
        int LEN_DATA_MAX = 1024;
    }
}
