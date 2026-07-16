package com.study.tddarchpractice.user.controller.response;

import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MyProfileResponseTest {
    @Test
    public void User으로_MyProfileResponse_응답을_생성할_수_있다() {
        // given
        User user = User.builder()
                .id(1L)
                .email("oh.youngsoo23@gmail.com")
                .nickname("ohyoungsoo")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .build();

        // when
        MyProfileResponse myProfileResponse = MyProfileResponse.from(user);

        // then
        assertThat(myProfileResponse.getId()).isEqualTo(1L);
        assertThat(myProfileResponse.getEmail()).isEqualTo("oh.youngsoo23@gmail.com");
        assertThat(myProfileResponse.getNickname()).isEqualTo("ohyoungsoo");
        assertThat(myProfileResponse.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}
