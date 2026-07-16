package com.study.tddarchpractice.user.domain;

import com.study.tddarchpractice.mock.TestClockHolder;
import com.study.tddarchpractice.mock.TestUuidHolder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserTest {

    @Test
    public void UserCreate_객체로_생성할_수_있다() {
        // given
        UserCreate userCreate = UserCreate.builder()
                .email("oh.youngsoo23@gmail.com")
                .nickname("ohyoungsoo")
                .address("Seoul")
                .build();

        // when
        User user = User.from(userCreate,new TestUuidHolder("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        // then
        assertThat(user.getEmail()).isEqualTo("oh.youngsoo23@gmail.com");
        assertThat(user.getNickname()).isEqualTo("ohyoungsoo");
        assertThat(user.getAddress()).isEqualTo("Seoul");
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
    }


    @Test
    public void UserUpdate_객체로_업데이트할_수_있다() {
        // given
        User user = User.builder()
                .id(1L)
                .email("oh.youngsoo23@gmail.com")
                .nickname("ohyoungsoo")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .build();

        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("newNickname")
                .address("Busan")
                .build();

        // when
        User updatedUser = user.update(userUpdate);

        // then
        assertThat(updatedUser.getEmail()).isEqualTo("oh.youngsoo23@gmail.com");
        assertThat(updatedUser.getNickname()).isEqualTo("newNickname");
        assertThat(updatedUser.getAddress()).isEqualTo("Busan");
    }

    @Test
    public void 로그인을_할_수_있고_마지막_로그인_시간이_업데이트된다() {
        // given
        User user = User.builder()
                .id(1L)
                .email("oh.youngsoo23@gmail.com")
                .nickname("ohyoungsoo")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .lastLoginAt(0L)
                .build();

        // when
        User loggedInUser = user.login(new TestClockHolder(1678530673958L));

        // then
        assertThat(loggedInUser.getLastLoginAt()).isEqualTo(1678530673958L);
    }

    @Test
    public void 유효한_인증_코드로_계정을_활성화_할_수_있다() {
        // given
        User user = User.builder()
                .id(1L)
                .email("oh.youngsoo23@gmail.com")
                .nickname("ohyoungsoo")
                .address("Seoul")
                .status(UserStatus.PENDING)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .build();

        // when
        User certifiedUser = user.certificate("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        // then
        assertThat(certifiedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(certifiedUser.getCertificationCode()).isEqualTo("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    }
}
