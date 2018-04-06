package com.noobug.nooblog.consts.error;

import com.noobug.nooblog.tools.entity.ErrorCode;

public class PublicError {

    public static final ErrorCode REQUEST_PARAM_ERROR = ErrorCode.of(-1000, "请求的参数有误");
    public static final ErrorCode REQUEST_BODY_PARAM_NULL = ErrorCode.of(-1001, "请求的Body参数有误");
    public static final ErrorCode REQUEST_PAGE_PARAM_ERROR = ErrorCode.of(-1002, "请求的分页参数有误");

    public static final ErrorCode SESSION_NO_USER = ErrorCode.of(-999, "用户未登录或登录已失效");
    public static final ErrorCode SESSION_NO_ADMIN = ErrorCode.of(-998, "管理员未登录或登录已失效");
}
