package com.study.tddarchpractice.user.controller.port;

import com.study.tddarchpractice.user.domain.User;

public interface UserReadService {
    User getByEmail(String email);

    User getById(long id);
}
