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

    @Test
    public void PostUpdate_로_게시물을_수정할수있다() {
        // given
        User writer = User.builder()
                .id(1L)
                .email("oh.youngsoo23@gmail.com")
                .nickname("ohyoungsoo")
                .address("Seoul")
                .status(ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .build();

        Post post = Post.builder()
                .id(1L)
                .content("This is a test post.")
                .createdAt(100L)
                .modifiedAt(100L)
                .writer(writer)
                .build();

        PostUpdate postUpdate = PostUpdate.builder()
                .content("This is updated content.")
                .build();

        // when
        Post updatedPost = post.update(postUpdate);

        // then
        assertThat(updatedPost.getId()).isEqualTo(1L);
        assertThat(updatedPost.getContent()).isEqualTo("This is updated content.");
        assertThat(updatedPost.getCreatedAt()).isEqualTo(100L);
        assertThat(updatedPost.getModifiedAt()).isGreaterThanOrEqualTo(post.getCreatedAt());
        assertThat(updatedPost.getWriter()).isEqualTo(writer);
    }

}
