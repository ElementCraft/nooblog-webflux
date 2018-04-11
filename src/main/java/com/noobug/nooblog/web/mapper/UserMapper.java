package com.noobug.nooblog.web.mapper;

import com.noobug.nooblog.domain.User;
import com.noobug.nooblog.web.dto.UserInfoDTO;
import com.noobug.nooblog.web.dto.UserRegDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    User regDTO2User(UserRegDTO regDTO);

    UserInfoDTO user2InfoDTO(User user);


}
