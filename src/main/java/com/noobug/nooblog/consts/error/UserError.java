package com.noobug.nooblog.consts.error;

import com.noobug.nooblog.tools.entity.ErrorCode;

public class UserError {
    public static final ErrorCode LOGIN_REQUIRE_IS_NULL = ErrorCode.of(1, "必填项不能为空");
    public static final ErrorCode LOGIN_NOT_EXIST_ACCOUNT = ErrorCode.of(2, "帐号不存在");
    public static final ErrorCode LOGIN_INCORRECT_PASSWORD = ErrorCode.of(3, "密码错误");
    public static final ErrorCode LOGIN_BANNED = ErrorCode.of(4, "该用户已被封禁，详情联系网站管理员");

    public static final ErrorCode DELETE_NON_EXIST_ID = ErrorCode.of(1, "不存在该用户");

    public static final ErrorCode COLUMN_TITLE_IS_NULL = ErrorCode.of(1, "栏目名称不能为空");
    public static final ErrorCode COLUMN_TITLE_TOO_LONG = ErrorCode.of(2, "栏目名称过长");
    public static final ErrorCode COLUMN_DUPLICATE_TITLE = ErrorCode.of(3, "同级目录下栏目名称不能重复");
    public static final ErrorCode COLUMN_PARENT_IS_NULL = ErrorCode.of(4, "指定的父级栏目不存在");
    ;
    public static final ErrorCode COLUMN_PARENT_NO_LEVEL1 = ErrorCode.of(5, "指定的父级栏目不是一级栏目");
    ;

    public static final ErrorCode REGIST_REQUIRE_IS_NULL = ErrorCode.of(1, "必填项不能为空");
    public static final ErrorCode REGIST_EXISTED_ACCOUNT = ErrorCode.of(2, "已存在的账号");
    public static final ErrorCode REGIST_ACCOUNT_LENGTH = ErrorCode.of(3, "帐号长度不符合要求");
    public static final ErrorCode REGIST_PASSWORD_LENGTH = ErrorCode.of(4, "密码长度不符合要求");
    public static final ErrorCode REGIST_ACCOUNT_EXIST_CHINESE = ErrorCode.of(5, "帐号不能使用中文字符");
    public static final ErrorCode REGIST_ACCOUNT_ALL_NUMBER = ErrorCode.of(6, "帐号不能为纯数字");
    public static final ErrorCode REGIST_ACCOUNT_EXIST_SPACE = ErrorCode.of(7, "帐号不能包含空格");
    public static final ErrorCode REGIST_PASSWORD_EXIST_SPACE = ErrorCode.of(8, "密码不能包含空格");
    public static final ErrorCode REGIST_NICKNAME_LENGTH = ErrorCode.of(9, "昵称长度不符合要求");
    public static final ErrorCode REGIST_NICKNAME_ALL_SPACE = ErrorCode.of(10, "昵称不能全为空格");
    public static final ErrorCode REGIST_EMAIL_INVALID = ErrorCode.of(11, "邮箱格式错误");


}
