package com.study.tddarchpractice.user.controller.response;

import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserResponseTest {
    @Test
    public void User으로_응답을_생성할_수_있다() {
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
        UserResponse userResponse = UserResponse.from(user);

        // then
        assertThat(userResponse.getId()).isEqualTo(1L);
        assertThat(userResponse.getEmail()).isEqualTo("oh.youngsoo23@gmail.com");
        assertThat(userResponse.getNickname()).isEqualTo("ohyoungsoo");
        assertThat(userResponse.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}
