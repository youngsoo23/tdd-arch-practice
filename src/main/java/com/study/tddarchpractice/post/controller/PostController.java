package com.study.tddarchpractice.post.controller;


import com.study.tddarchpractice.post.controller.port.PostReadService;
import com.study.tddarchpractice.post.controller.port.PostUpdateService;
import com.study.tddarchpractice.post.domain.Post;
import com.study.tddarchpractice.user.controller.UserController;
import com.study.tddarchpractice.post.controller.response.PostResponse;
import com.study.tddarchpractice.post.domain.PostUpdate;
import com.study.tddarchpractice.post.infrastructure.PostEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시물(posts)")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostReadService postReadService;
    private final PostUpdateService postUpdateService;
    private final UserController userController;

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable long id) {
        return ResponseEntity
            .ok()
            .body(PostResponse.from(postReadService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable long id, @RequestBody PostUpdate postUpdate) {
        return ResponseEntity
            .ok()
            .body(PostResponse.from(postUpdateService.update(id, postUpdate)));
    }


}