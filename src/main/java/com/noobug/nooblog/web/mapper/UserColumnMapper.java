package com.noobug.nooblog.web.mapper;

import com.noobug.nooblog.domain.Article;
import com.noobug.nooblog.domain.UserColumn;
import com.noobug.nooblog.web.dto.AddNewArticleDTO;
import com.noobug.nooblog.web.dto.AddUserColumnDTO;
import com.noobug.nooblog.web.dto.ArticleListDTO;
import com.noobug.nooblog.web.dto.UserColumnInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * 用户栏目 相关 Mapper
 *
 * @author noobug.com
 */
@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, uses = {UserMapper.class})
public interface UserColumnMapper extends EntityMapper<AddUserColumnDTO, UserColumn> {

}
