package com.study.tddarchpractice.user.controller.port;

import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.domain.UserCreate;
import com.study.tddarchpractice.user.domain.UserUpdate;

public interface UserService {
    User getByEmail(String email);

    User getById(long id);

    User create(UserCreate userCreate);

    User update(long id, UserUpdate userUpdate);

    void login(long id);

    void verifyEmail(long id, String certificationCode);
}
