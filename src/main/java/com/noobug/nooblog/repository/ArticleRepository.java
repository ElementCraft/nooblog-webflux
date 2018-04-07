package com.noobug.nooblog.repository;

import com.noobug.nooblog.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findAllByStatusNot(int status, Pageable pageable);

    Page<Article> findAllByUserColumnIdAndStatusNot(long id, int status, Pageable pageable);

    Page<Article> findAllByUserColumnUserIdAndStatusNot(long id, int status, Pageable pageable);

    Page<Article> findAllByIsPrivateAndStatusNotOrderByGmtCreateDesc(Boolean isPrivate, int status, Pageable pageable);
}
