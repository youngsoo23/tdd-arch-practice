package com.study.tddarchpractice.user.service;

import com.study.tddarchpractice.common.service.port.UuidHolder;
import com.study.tddarchpractice.user.controller.port.UserCreateService;
import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.domain.UserCreate;
import com.study.tddarchpractice.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCreateServiceImpl implements UserCreateService {

    private final UserRepository userRepository;
    private final CertificationService certificationService;
    private final UuidHolder uuidHolder;

    @Override
    @Transactional
    public User create(UserCreate userCreate) {
        User user = userRepository.save(User.from(userCreate, uuidHolder));
        certificationService.send(userCreate.getEmail(), user.getId(), user.getCertificationCode());
        return user;
    }
}
