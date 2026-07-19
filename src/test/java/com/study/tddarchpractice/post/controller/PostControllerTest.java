package com.study.tddarchpractice.post.controller;

import com.study.tddarchpractice.mock.FakePostService;
import com.study.tddarchpractice.post.controller.response.PostResponse;
import com.study.tddarchpractice.post.domain.Post;
import com.study.tddarchpractice.post.domain.PostUpdate;
import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PostControllerTest {

    private PostController postController;
    private FakePostService fakePostService;

    @BeforeEach
    void init() {
        fakePostService = new FakePostService();
        postController = new PostController(fakePostService, null);
    }

    @Test
    void getPostById는_게시물을_조회할수있다() {
        // given
        User writer = User.builder()
                .id(1L)
                .email("oh.youngsoo23@gmail.com")
                .nickname("ohyoungsoo")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .build();
        Post post = fakePostService.save(Post.builder()
                .content("This is the content of the first post.")
                .writer(writer)
                .createdAt(1678530673958L)
                .build());
        // when
        ResponseEntity<PostResponse> response = postController.getPostById(post.getId());
        // then
        assertThat(response.getBody().getContent()).isEqualTo("This is the content of the first post.");
    }

    @Test
    void updatePost는_게시물을_수정할수있다() {
        // given
        User writer = User.builder()
                .id(1L)
                .email("oh.youngsoo23@gmail.com")
                .nickname("ohyoungsoo")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .build();
        Post post = fakePostService.save(Post.builder()
                .content("This is the content of the first post.")
                .writer(writer)
                .createdAt(1678530673958L)
                .build());
        // when
        ResponseEntity<PostResponse> response = postController.updatePost(post.getId(), new PostUpdate("This is an updated post."));
        // then
        assertThat(response.getBody().getContent()).isEqualTo("This is an updated post.");
    }
}
