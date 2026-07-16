package com.study.tddarchpractice.user.service;

import com.study.tddarchpractice.common.domain.exception.CertificationCodeNotMatchedException;
import com.study.tddarchpractice.common.domain.exception.ResourceNotFoundException;
import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.domain.UserStatus;
import com.study.tddarchpractice.user.domain.UserCreate;
import com.study.tddarchpractice.user.domain.UserUpdate;
import com.study.tddarchpractice.user.infrastructure.UserEntity;
import com.study.tddarchpractice.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CertificationService certificationService;


    public User getByEmail(String email) {
        return userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Users", email));
    }

    public User getById(long id) {
        return userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Users", id));
    }

    @Transactional
    public User create(UserCreate userCreate) {
        User user = userRepository.save(User.from(userCreate));
        certificationService.send(userCreate.getEmail(), user.getId(), user.getCertificationCode());
        return user;
    }

    @Transactional
    public User update(long id, UserUpdate userUpdate) {
        User user = getById(id);
        user = user.update(userUpdate);
        user = userRepository.save(user);
        return user;
    }

    @Transactional
    public void login(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Users", id));
        user.login();
        userRepository.save(user); //jpa 의존성이 사라져서 저장해줘야한다.
    }

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