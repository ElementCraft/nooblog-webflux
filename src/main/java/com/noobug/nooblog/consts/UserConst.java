package com.noobug.nooblog.consts;

public interface UserConst {

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "USER";

    /**
     * 性别枚举
     */
    interface Sex {
        /**
         * 未知
         */
        int UNKNOWN = 0;

        /**
         * 男性
         */
        int MALE = 1;

        /**
         * 女性
         */
        int FEMALE = 2;

        /**
         * 保密
         */
        int SECRET = 3;
    }
}
