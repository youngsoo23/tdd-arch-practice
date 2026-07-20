package com.study.tddarchpractice.post.controller;

import com.study.tddarchpractice.mock.TestContainer;
import com.study.tddarchpractice.post.controller.response.PostResponse;
import com.study.tddarchpractice.post.domain.PostCreate;
import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PostCreateControllerTest {

    private TestContainer testContainer;

    @BeforeEach
    void init() {
        testContainer = TestContainer.builder().build();

        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("oh.youngsoo23@gmail.com")
                .nickname("ohyoungsoo")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .build());
    }

    @Test
    void createPost는_게시물을_생성하고_201로_응답한다() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("This is a new post.")
                .build();
        // when
        ResponseEntity<PostResponse> response = testContainer.postCreateController.createPost(postCreate);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getContent()).isEqualTo("This is a new post.");
        assertThat(response.getBody().getWriter().getNickname()).isEqualTo("ohyoungsoo");
    }
}
