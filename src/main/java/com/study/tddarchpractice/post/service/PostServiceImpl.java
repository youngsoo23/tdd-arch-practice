package com.study.tddarchpractice.post.service;

import com.study.tddarchpractice.common.domain.exception.ResourceNotFoundException;
import com.study.tddarchpractice.post.controller.port.PostService;
import com.study.tddarchpractice.post.domain.Post;
import com.study.tddarchpractice.post.domain.PostCreate;
import com.study.tddarchpractice.post.domain.PostUpdate;
import com.study.tddarchpractice.post.infrastructure.PostEntity;
import com.study.tddarchpractice.post.infrastructure.PostJpaRepository;
import com.study.tddarchpractice.post.service.port.PostRepository;
import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.infrastructure.UserEntity;
import com.study.tddarchpractice.user.service.port.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Builder
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public Post getById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Posts", id));
    }

    @Override
    public Post create(PostCreate postCreate) {
        User user = userRepository.getById(postCreate.getWriterId());
        Post post = Post.builder()
                .writer(user)
                .content(postCreate.getContent())
                .createdAt(Clock.systemUTC().millis())
                .build();
        return postRepository.save(post);
    }

    @Override
    public Post update(long id, PostUpdate postUpdate) {
        Post post = getById(id);
        post = post.update(postUpdate);
        return postRepository.save(post);
    }
}