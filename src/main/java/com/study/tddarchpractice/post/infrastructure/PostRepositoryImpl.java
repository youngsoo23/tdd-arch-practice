package com.study.tddarchpractice.post.infrastructure;

import com.study.tddarchpractice.post.domain.Post;
import com.study.tddarchpractice.post.service.port.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private final PostJpaRepository postJpaRepository;

    @Override
    public Optional<Post> findById(Long id) {
        return postJpaRepository. findById(id).map(PostEntity::toModel);
    }

    @Override
    public Post save(Post post) {
        return postJpaRepository.save(PostEntity.fromModel(post)).toModel();
    }
}
