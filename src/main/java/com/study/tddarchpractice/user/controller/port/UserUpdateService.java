package com.study.tddarchpractice.user.controller.port;

import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.domain.UserUpdate;

public interface UserUpdateService {
    User update(long id, UserUpdate userUpdate);
}
