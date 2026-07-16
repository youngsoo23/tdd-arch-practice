package com.study.tddarchpractice.post.controller.response;

import com.study.tddarchpractice.post.domain.Post;
import com.study.tddarchpractice.user.controller.response.UserResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostResponse {

    private Long id;
    private String content;
    private Long createdAt;
    private Long modifiedAt;
    private UserResponse writer;

    public static PostResponse from(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setContent(post.getContent());
        response.setCreatedAt(post.getCreatedAt());
        response.setModifiedAt(post.getModifiedAt());
        response.setWriter(UserResponse.from(post.getWriter()));
        return response;
    }
}
