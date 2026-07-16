package com.study.tddarchpractice.post.service.port;

import com.study.tddarchpractice.post.domain.Post;

import java.util.Optional;

public interface PostRepository {
    Optional<Post> findById(Long id);

    Post save(Post post);
}
