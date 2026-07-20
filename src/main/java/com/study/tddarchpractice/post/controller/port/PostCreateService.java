package com.study.tddarchpractice.post.controller.port;

import com.study.tddarchpractice.post.domain.Post;
import com.study.tddarchpractice.post.domain.PostCreate;

public interface PostCreateService {
    Post create(PostCreate postCreate);
}
