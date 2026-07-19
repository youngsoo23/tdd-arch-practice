package com.study.tddarchpractice.user.controller;

import com.study.tddarchpractice.mock.FakeUserService;
import com.study.tddarchpractice.user.controller.response.MyProfileResponse;
import com.study.tddarchpractice.user.controller.response.UserResponse;
import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.domain.UserStatus;
import com.study.tddarchpractice.user.domain.UserUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserControllerTest {

    private UserController userController;
    private FakeUserService fakeUserService;

    @BeforeEach
    void init() {
        fakeUserService = new FakeUserService();
        userController = new UserController(fakeUserService);

        fakeUserService.save(User.builder()
                .id(1L)
                .email("oh.youngsoo23@gmail.com")
                .nickname("ohyoungsoo")
                .address("Seoul")
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .status(UserStatus.ACTIVE)
                .build());

        fakeUserService.save(User.builder()
                .id(2L)
                .email("oh.youngsoo223@gmail.com")
                .nickname("ohyoungsoo2")
                .address("Seoul")
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .status(UserStatus.PENDING)
                .build());
    }

    @Test
    void getUserById는_유저를_조회할수있다() {
        // given
        // when
        ResponseEntity<UserResponse> response = userController.getUserById(1);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNickname()).isEqualTo("ohyoungsoo");
    }

    @Test
    void verifyEmail은_인증코드가_일치하면_302로_응답한다() {
        // given
        // when
        ResponseEntity<Void> response = userController.verifyEmail(2, "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(fakeUserService.getById(2).getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void getMyInfo는_EMAIL_헤더로_내정보를_조회하고_로그인시간을_갱신한다() {
        // given
        // when
        ResponseEntity<MyProfileResponse> response = userController.getMyInfo("oh.youngsoo23@gmail.com");
        // then
        assertThat(response.getBody().getNickname()).isEqualTo("ohyoungsoo");
        assertThat(fakeUserService.getById(1).getLastLoginAt()).isEqualTo(1678530673958L);
    }

    @Test
    void updateMyInfo는_내정보를_수정할수있다() {
        // given
        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("ohyoungsoo12")
                .address("Seoul Nowon")
                .build();
        // when
        ResponseEntity<MyProfileResponse> response = userController.updateMyInfo("oh.youngsoo23@gmail.com", userUpdate);
        // then
        assertThat(response.getBody().getNickname()).isEqualTo("ohyoungsoo12");
        assertThat(response.getBody().getAddress()).isEqualTo("Seoul Nowon");
    }
}
