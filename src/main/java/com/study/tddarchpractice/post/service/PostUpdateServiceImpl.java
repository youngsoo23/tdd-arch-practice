package com.study.tddarchpractice.post.service;

import com.study.tddarchpractice.post.controller.port.PostReadService;
import com.study.tddarchpractice.post.controller.port.PostUpdateService;
import com.study.tddarchpractice.post.domain.Post;
import com.study.tddarchpractice.post.domain.PostUpdate;
import com.study.tddarchpractice.post.service.port.PostRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Builder
public class PostUpdateServiceImpl implements PostUpdateService {

    private final PostRepository postRepository;
    private final PostReadService postReadService;

    @Override
    public Post update(long id, PostUpdate postUpdate) {
        Post post = postReadService.getById(id);
        post = post.update(postUpdate);
        return postRepository.save(post);
    }
}
