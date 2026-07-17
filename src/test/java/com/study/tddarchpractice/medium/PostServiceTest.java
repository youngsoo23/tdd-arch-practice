package com.study.tddarchpractice.medium;

import com.study.tddarchpractice.post.domain.Post;
import com.study.tddarchpractice.post.domain.PostCreate;
import com.study.tddarchpractice.post.domain.PostUpdate;
import com.study.tddarchpractice.post.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestPropertySource("classpath:test-application.yml")
@SqlGroup({
        @Sql(scripts = "/sql/post-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "/sql/clear-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @Test
    void getById는_존재는_게시물을_내려준다() {
        // given
        // when
        Post post = postService.getById(1);
        // then
        assertThat(post.getContent())
                .isEqualTo("This is the content of the first post.");
    }

    @Test
    void create는_게시물을_생성한다() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("This is a new post.")
                .build();
        // when
        Post post = postService.create(postCreate);
        // then
        assertThat(post.getId()).isNotNull();
        assertThat(post.getContent()).isEqualTo("This is a new post.");
    }

    @Test
    void update는_게시물을_수정한다() {
        // given
        // when
        Post post = postService.update(1, new PostUpdate( "This is an updated post."));
        // then
        assertThat(post.getContent())
                .isEqualTo("This is an updated post.");
    }
}
