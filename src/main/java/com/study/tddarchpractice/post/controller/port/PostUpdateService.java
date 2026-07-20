package com.study.tddarchpractice.post.controller.port;

import com.study.tddarchpractice.post.domain.Post;
import com.study.tddarchpractice.post.domain.PostUpdate;

public interface PostUpdateService {
    Post update(long id, PostUpdate postUpdate);
}
