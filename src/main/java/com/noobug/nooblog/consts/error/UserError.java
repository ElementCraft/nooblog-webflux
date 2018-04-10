package com.noobug.nooblog.consts.error;

import com.noobug.nooblog.tools.entity.ErrorCode;

/**
 * User接口相关错误枚举
 *
 * @author noobug.com
 */
public interface UserError {

    ErrorCode NON_EXIST_ID = ErrorCode.of(1, "不存在该用户");

    /**
     * 登录相关错误
     */
    interface Login {
        ErrorCode REQUIRE_IS_NULL = ErrorCode.of(1, "必填项不能为空");
        ErrorCode NOT_EXIST_ACCOUNT = ErrorCode.of(2, "帐号不存在");
        ErrorCode INCORRECT_PASSWORD = ErrorCode.of(3, "密码错误");
        ErrorCode BANNED = ErrorCode.of(4, "该用户已被封禁，详情联系网站管理员");
    }

    /**
     * 注册相关错误
     */
    interface Reg {
        ErrorCode REQUIRE_IS_NULL = ErrorCode.of(1, "必填项不能为空");
        ErrorCode EXISTED_ACCOUNT = ErrorCode.of(2, "已存在的账号");
        ErrorCode ACCOUNT_LENGTH = ErrorCode.of(3, "帐号长度不符合要求");
        ErrorCode PASSWORD_LENGTH = ErrorCode.of(4, "密码长度不符合要求");
        ErrorCode ACCOUNT_EXIST_CHINESE = ErrorCode.of(5, "帐号不能使用中文字符");
        ErrorCode ACCOUNT_ALL_NUMBER = ErrorCode.of(6, "帐号不能为纯数字");
        ErrorCode ACCOUNT_EXIST_SPACE = ErrorCode.of(7, "帐号不能包含空格");
        ErrorCode PASSWORD_EXIST_SPACE = ErrorCode.of(8, "密码不能包含空格");
        ErrorCode NICKNAME_LENGTH = ErrorCode.of(9, "昵称长度不符合要求");
        ErrorCode NICKNAME_ALL_SPACE = ErrorCode.of(10, "昵称不能全为空格");
        ErrorCode EMAIL_INVALID = ErrorCode.of(11, "邮箱格式错误");
    }

    /**
     * 用户栏目相关错误
     */
    interface Column {
        ErrorCode TITLE_IS_NULL = ErrorCode.of(1, "栏目名称不能为空");
        ErrorCode TITLE_TOO_LONG = ErrorCode.of(2, "栏目名称过长");
        ErrorCode DUPLICATE_TITLE = ErrorCode.of(3, "同级目录下栏目名称不能重复");
        ErrorCode PARENT_IS_NULL = ErrorCode.of(4, "指定的父级栏目不存在");
        ErrorCode PARENT_NO_LEVEL1 = ErrorCode.of(5, "指定的父级栏目不是一级栏目");
    }

}
