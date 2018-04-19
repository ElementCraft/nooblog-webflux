package com.noobug.nooblog.repository;

import com.noobug.nooblog.domain.Article;
import com.noobug.nooblog.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
