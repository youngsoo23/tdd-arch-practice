package com.study.tddarchpractice.post.domain;

import com.study.tddarchpractice.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Post {
    private Long id;

    private String content;

    private Long createdAt;

    private Long modifiedAt;

    private User writer;

    @Builder
    public Post(Long id, String content, Long createdAt, Long modifiedAt, User writer) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.writer = writer;
    }

    public static Post from(PostCreate postCreate, User writer) {
        return Post.builder()
                .content(postCreate.getContent())
                .createdAt(System.currentTimeMillis())
                .modifiedAt(System.currentTimeMillis())
                .writer(writer)
                .build();
    }

    public Post update(PostUpdate postUpdate) {
        return Post.builder()
                .id(this.id)
                .content(postUpdate.getContent())
                .createdAt(this.createdAt)
                .modifiedAt(System.currentTimeMillis())
                .writer(this.writer)
                .build();
    }
}
