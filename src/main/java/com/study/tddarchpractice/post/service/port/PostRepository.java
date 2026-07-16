package com.study.tddarchpractice.post.service.port;

import com.study.tddarchpractice.post.infrastructure.PostEntity;

import java.util.Optional;

public interface PostRepository {
    Optional<PostEntity> findById(Long id);

    PostEntity save(PostEntity postEntity);
}
