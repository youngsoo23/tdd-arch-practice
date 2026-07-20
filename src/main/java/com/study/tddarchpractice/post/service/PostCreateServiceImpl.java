package com.study.tddarchpractice.post.service;

import com.study.tddarchpractice.post.controller.port.PostCreateService;
import com.study.tddarchpractice.post.domain.Post;
import com.study.tddarchpractice.post.domain.PostCreate;
import com.study.tddarchpractice.post.service.port.PostRepository;
import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.service.port.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Builder
public class PostCreateServiceImpl implements PostCreateService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

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
}
