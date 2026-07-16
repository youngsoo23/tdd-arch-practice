package com.study.tddarchpractice.user.service.port;

import com.study.tddarchpractice.user.domain.UserStatus;
import com.study.tddarchpractice.user.infrastructure.UserEntity;

import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> findByIdAndStatus(long id, UserStatus userStatus);

    Optional<UserEntity> findByEmailAndStatus(String email, UserStatus userStatus);

    UserEntity save(UserEntity userEntity);

    Optional<UserEntity> findById(long id);
}
