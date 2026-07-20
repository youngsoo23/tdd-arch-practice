package com.study.tddarchpractice.post.controller.port;

import com.study.tddarchpractice.post.domain.Post;

public interface PostReadService {
    Post getById(long id);
}
