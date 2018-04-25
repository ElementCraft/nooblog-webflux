package com.noobug.nooblog.web.mapper;

import com.noobug.nooblog.domain.UserLog;
import com.noobug.nooblog.web.dto.UserLogInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

/**
 * 用户登录日志 相关 Mapper
 *
 * @author noobug.com
 */
@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, uses = {UserMapper.class})
public interface UserLogMapper extends EntityMapper<UserLogInfoDTO, UserLog> {

}
