package com.study.tddarchpractice.post.service;

import com.study.tddarchpractice.common.domain.exception.ResourceNotFoundException;
import com.study.tddarchpractice.mock.TestContainer;
import com.study.tddarchpractice.post.domain.Post;
import com.study.tddarchpractice.post.domain.PostCreate;
import com.study.tddarchpractice.post.domain.PostUpdate;
import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PostServiceTest {

    private TestContainer testContainer;

    @BeforeEach
    void init() {
        testContainer = TestContainer.builder().build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("oh.youngsoo23@gmail.com")
                .nickname("ohyoungsoo")
                .address("Seoul")
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .status(UserStatus.ACTIVE)
                .build());
    }

    @Test
    void getById는_존재하는_게시물을_내려준다() {
        // given
        User writer = testContainer.userRepository.findById(1).get();
        Post post = testContainer.postRepository.save(Post.builder()
                .content("This is the content of the first post.")
                .writer(writer)
                .createdAt(1678530673958L)
                .build());
        // when
        Post result = testContainer.postService.getById(post.getId());
        // then
        assertThat(result.getContent()).isEqualTo("This is the content of the first post.");
    }

    @Test
    void getById는_존재하지_않는_게시물이면_ResourceNotFoundException_발생() {
        // given
        // when
        // then
        assertThatThrownBy(() -> testContainer.postService.getById(1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create는_게시물을_생성한다() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("This is a new post.")
                .build();
        // when
        Post post = testContainer.postService.create(postCreate);
        // then
        assertThat(post.getId()).isNotNull();
        assertThat(post.getContent()).isEqualTo("This is a new post.");
        assertThat(post.getWriter().getEmail()).isEqualTo("oh.youngsoo23@gmail.com");
    }

    @Test
    void create는_존재하지_않는_작성자면_ResourceNotFoundException_발생() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(999)
                .content("This is a new post.")
                .build();
        // when
        // then
        assertThatThrownBy(() -> testContainer.postService.create(postCreate))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update는_게시물을_수정한다() {
        // given
        User writer = testContainer.userRepository.findById(1).get();
        Post post = testContainer.postRepository.save(Post.builder()
                .content("This is the content of the first post.")
                .writer(writer)
                .createdAt(1678530673958L)
                .build());
        // when
        Post result = testContainer.postService.update(post.getId(), new PostUpdate("This is an updated post."));
        // then
        assertThat(result.getContent()).isEqualTo("This is an updated post.");
    }
}
