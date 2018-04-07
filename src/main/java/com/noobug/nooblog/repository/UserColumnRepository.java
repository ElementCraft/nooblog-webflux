package com.noobug.nooblog.repository;

import com.noobug.nooblog.domain.UserColumn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserColumnRepository extends JpaRepository<UserColumn, Long> {

    UserColumn findOneByUserIdAndIsDefault(Long id, Boolean isDefault);

    List<UserColumn> findAllByUser_IdAndIsDefaultAndParentIdIsNullOrParentId(long id, Boolean isDefault, long parentId);

    List<UserColumn> findAllByParentIdAndIsDefault(Long id, Boolean isDefault);

    Long countByUser_IdAndTitle(Long id, String title);

    Long countByParentIdAndTitle(Long parentId, String title);
}
