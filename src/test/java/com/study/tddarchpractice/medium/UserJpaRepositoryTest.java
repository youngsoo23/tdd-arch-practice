package com.study.tddarchpractice.medium;


import com.study.tddarchpractice.user.domain.UserStatus;
import com.study.tddarchpractice.user.infrastructure.UserEntity;
import com.study.tddarchpractice.user.infrastructure.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//note 전채 테스트를 돌리면 성공하지 않는데
//원인
// 테스트 메소드가 병렬로 처리되는데 동시성 제어가 안되는거 같아서.
@DataJpaTest
@Sql(scripts = "/sql/user-repository-test-data.sql")
public class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJPARepository;

    @Test
    void findByIdAndStatus_유저_데이터를_찾아올수있다() {
        // given
        //<--------------- 값 없애고 테스트 전에 값미리 등록 (@Sql 활용)----------->
//        UserEntity userEntity = UserEntity.builder()
//                .email("oh.youngsoo23@gmail.com")
//                .address("서울시 강남구")
//                .nickname("오영수")
//                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
//                .status(UserStatus.PENDING)
//                .build();
//        userRepository.save(userEntity);
        // when
        Optional<UserEntity> result = userJPARepository.findByIdAndStatus(1, UserStatus.ACTIVE);
        // then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void findByIdAndStatus_유저데이터가_없으면_Optional_empty를_내려준다() {
//        // given
//        UserEntity userEntity = UserEntity.builder()
//                .email("oh.youngsoo23@gmail.com")
//                .address("서울시 강남구")
//                .nickname("오영수")
//                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
//                .status(UserStatus.PENDING)
//                .build();
//        userRepository.save(userEntity);
        // when
        Optional<UserEntity> result = userJPARepository.findByIdAndStatus(1, UserStatus.PENDING);
        // then
        assertThat(result.isEmpty()).isTrue();

    }

    @Test
    void findByEmailAndStatus_유저_데이터를_찾아올수있다() {
//        // given
//        UserEntity userEntity = UserEntity.builder()
//                .email("oh.youngsoo23@gmail.com")
//                .address("서울시 강남구")
//                .nickname("오영수")
//                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
//                .status(UserStatus.PENDING)
//                .build();
//        userRepository.save(userEntity);
        // when
        Optional<UserEntity> result = userJPARepository.findByEmailAndStatus("oh.youngsoo23@gmail.com", UserStatus.ACTIVE);
        // then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void findByEmailAndStatus_유저데이터가_없으면_Optional_empty를_내려준다() {
//        // given
//        UserEntity userEntity = UserEntity.builder()
//                .email("oh.youngsoo23@gmail.com")
//                .address("서울시 강남구")
//                .nickname("오영수")
//                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
//                .status(UserStatus.PENDING)
//                .build();
//        userRepository.save(userEntity);
        // when
        Optional<UserEntity> result = userJPARepository.findByEmailAndStatus("oh.youngsoo@gmail.com", UserStatus.PENDING);
        // then
        assertThat(result.isEmpty()).isTrue();
    }
}
