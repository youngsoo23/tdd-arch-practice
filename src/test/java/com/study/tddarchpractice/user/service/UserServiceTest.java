package com.study.tddarchpractice.user.service;

import com.study.tddarchpractice.common.domain.exception.CertificationCodeNotMatchedException;
import com.study.tddarchpractice.common.domain.exception.ResourceNotFoundException;
import com.study.tddarchpractice.mock.TestContainer;
import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.domain.UserCreate;
import com.study.tddarchpractice.user.domain.UserStatus;
import com.study.tddarchpractice.user.domain.UserUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UserServiceTest {

    private TestContainer testContainer;

    @BeforeEach
    void init() {
        testContainer = TestContainer.builder().build();

        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("oh.youngsoo23@gmail.com")
                .nickname("ohyoungsoo")
                .address("Seoul")
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .status(UserStatus.ACTIVE)
                .build());
        testContainer.userRepository.save(User.builder()
                .id(2L)
                .email("oh.youngsoo223@gmail.com")
                .nickname("ohyoungsoo2")
                .address("Seoul")
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .status(UserStatus.PENDING)
                .build());
    }

    @Test
    void getByEmail은_ACTIVE_상태의_유저를_조회할수있다() {
        // given
        String email = "oh.youngsoo23@gmail.com";
        // when
        User user = testContainer.userReadService.getByEmail(email);
        // then
        assertThat(user.getNickname()).isEqualTo("ohyoungsoo");
    }

    @Test
    void getByEmail은_PENDING_상태인_유저는_찾아올수없다() {
        // given
        String email = "oh.youngsoo223@gmail.com";
        // when
        // then
        assertThatThrownBy(() -> testContainer.userReadService.getByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getById는_ACTIVE_상태의_유저를_조회할수있다() {
        // given
        // when
        User user = testContainer.userReadService.getById(1);
        // then
        assertThat(user.getNickname()).isEqualTo("ohyoungsoo");
    }

    @Test
    void getById는_PENDING_상태의_유저를_조회할수없다() {
        // given
        // when
        // then
        assertThatThrownBy(() -> testContainer.userReadService.getById(2))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void userCreateDto_를_이용한_유저_생성할수있다() {
        // given
        UserCreate userCreate = UserCreate.builder()
                .email("oh.youngsoo234@gmail.com")
                .nickname("ohyoungsoo1")
                .address("Seoul")
                .build();
        // when
        User user = testContainer.userCreateService.create(userCreate);
        // then
        assertThat(user.getId()).isNotNull();
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getCertificationCode()).isEqualTo("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        assertThat(testContainer.mailSender.email).isEqualTo("oh.youngsoo234@gmail.com");
        assertThat(testContainer.mailSender.subject).isEqualTo("Please certify your email address");
    }

    @Test
    void userUpdate_를_이용한_유저_수정할수있다() {
        // given
        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("ohyoungsoo12")
                .address("Seoul Nowon")
                .build();
        // when
        testContainer.userUpdateService.update(1, userUpdate);
        // then
        User user = testContainer.userReadService.getById(1);
        assertThat(user.getId()).isNotNull();
        assertThat(user.getNickname()).isEqualTo("ohyoungsoo12");
        assertThat(user.getAddress()).isEqualTo("Seoul Nowon");
    }

    @Test
    void login_테스트_마지막_로그인시간_저장() {
        // given
        // when
        testContainer.userAuthenticationService.login(1);
        // then
        User user = testContainer.userReadService.getById(1);
        assertThat(user.getLastLoginAt()).isEqualTo(1678530673958L);
    }

    @Test
    void PENDING_상태의_유저는_이메일_인증을_통해_ACTIVE_상태로_변경할수있다() {
        // given
        // when
        testContainer.userAuthenticationService.verifyEmail(2, "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        // then
        User user = testContainer.userReadService.getById(2);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void PENDING_상태의_유저는_잘못된_인증코드로_인증을_시도하면_CertificationCodeNotMatchedException_발생() {
        // given
        // when
        // then
        assertThatThrownBy(() -> testContainer.userAuthenticationService.verifyEmail(2, "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}
