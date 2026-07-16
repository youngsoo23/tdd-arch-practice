package com.study.tddarchpractice.post.controller.response;

import com.study.tddarchpractice.post.domain.Post;
import com.study.tddarchpractice.user.domain.User;
import org.junit.jupiter.api.Test;

import static com.study.tddarchpractice.user.domain.UserStatus.ACTIVE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PostResponseTest {
    @Test
    public void Post로_응답을_생성할_수_있다() {
        // given
        Post post = Post.builder()
                .content("This is a test post.")
                .writer(User.builder()
                        .email("oh.youngsoo23@gmail.com")
                        .nickname("ohyoungsoo")
                        .address("Seoul")
                        .status(ACTIVE)
                        .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                        .build())
                .build();

        // when
        PostResponse postResponse = PostResponse.from(post);

        //then
        assertThat(postResponse.getContent()).isEqualTo("This is a test post.");
        assertThat(postResponse.getWriter().getEmail()).isEqualTo("oh.youngsoo23@gmail.com");
        assertThat(postResponse.getWriter().getNickname()).isEqualTo("ohyoungsoo");
        assertThat(postResponse.getWriter().getStatus()).isEqualTo(ACTIVE);

    }
}
