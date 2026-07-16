package com.study.tddarchpractice.post.domain;

import com.study.tddarchpractice.user.domain.User;
import org.junit.jupiter.api.Test;

import static com.study.tddarchpractice.user.domain.UserStatus.ACTIVE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PostTest {

    @Test
    public void PostCreate_로_게시물_생성_테스트() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("This is a test post.")
                .build();

        User writer = User.builder()
                .id(postCreate.getWriterId())
                .email("oh.youngsoo23@gmail.com")
                .nickname("ohyoungsoo")
                .address("Seoul")
                .status(ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .build();

        //when
        Post post = Post.from(postCreate, writer);

        // then
        assertThat(post.getContent()).isEqualTo("This is a test post.");
        assertThat(post.getWriter().getId()).isEqualTo(1);
        assertThat(post.getWriter().getEmail()).isEqualTo("oh.youngsoo23@gmail.com");
        assertThat(post.getWriter().getNickname()).isEqualTo("ohyoungsoo");
        assertThat(post.getWriter().getStatus()).isEqualTo(ACTIVE);
    }

}
