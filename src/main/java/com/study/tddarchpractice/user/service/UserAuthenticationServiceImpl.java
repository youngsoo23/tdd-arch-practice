package com.study.tddarchpractice.user.service;

import com.study.tddarchpractice.common.domain.exception.CertificationCodeNotMatchedException;
import com.study.tddarchpractice.common.domain.exception.ResourceNotFoundException;
import com.study.tddarchpractice.common.service.port.ClockHolder;
import com.study.tddarchpractice.user.controller.port.UserAuthenticationService;
import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final UserRepository userRepository;
    private final ClockHolder clockHolder;

    @Override
    @Transactional
    public void login(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Users", id));
        user = user.login(clockHolder);
        userRepository.save(user); //jpa 의존성이 사라져서 저장해줘야한다.
    }

    @Override
    @Transactional
    public void verifyEmail(long id, String certificationCode) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Users", id));
        if (!certificationCode.equals(user.getCertificationCode())) {
            throw new CertificationCodeNotMatchedException();
        }
        user = user.certificate(certificationCode);
        userRepository.save(user);
    }
}
