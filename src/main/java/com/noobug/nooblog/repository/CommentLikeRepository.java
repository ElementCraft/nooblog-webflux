package com.noobug.nooblog.repository;

import com.noobug.nooblog.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

}
