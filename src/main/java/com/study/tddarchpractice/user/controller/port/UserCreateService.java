package com.study.tddarchpractice.user.controller.port;

import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.domain.UserCreate;

public interface UserCreateService {
    User create(UserCreate userCreate);
}
