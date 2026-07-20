package com.study.tddarchpractice.user.service;

import com.study.tddarchpractice.user.controller.port.UserReadService;
import com.study.tddarchpractice.user.controller.port.UserUpdateService;
import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.domain.UserUpdate;
import com.study.tddarchpractice.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserUpdateServiceImpl implements UserUpdateService {

    private final UserRepository userRepository;
    private final UserReadService userReadService;

    @Override
    @Transactional
    public User update(long id, UserUpdate userUpdate) {
        User user = userReadService.getById(id);
        user = user.update(userUpdate);
        user = userRepository.save(user);
        return user;
    }
}
