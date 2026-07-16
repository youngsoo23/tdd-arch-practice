package com.study.tddarchpractice.post.service;

import com.study.tddarchpractice.common.domain.exception.ResourceNotFoundException;
import com.study.tddarchpractice.post.domain.Post;
import com.study.tddarchpractice.post.domain.PostCreate;
import com.study.tddarchpractice.post.domain.PostUpdate;
import com.study.tddarchpractice.post.infrastructure.PostEntity;
import com.study.tddarchpractice.post.infrastructure.PostJpaRepository;
import com.study.tddarchpractice.post.service.port.PostRepository;
import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.infrastructure.UserEntity;
import com.study.tddarchpractice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    public Post getById(long id) {
        return postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Posts", id));
    }

    public Post create(PostCreate postCreate) {
        User user = userService.getById(postCreate.getWriterId());
        Post post = Post.builder()
                .writer(user)
                .content(postCreate.getContent())
                .createdAt(Clock.systemUTC().millis())
                .build();
        return postRepository.save(post);
    }

    public Post update(long id, PostUpdate postUpdate) {
        Post post = getById(id);
        post = post.update(postUpdate);
        return postRepository.save(post);
    }
}