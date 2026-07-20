package com.study.tddarchpractice.user.controller;

import com.study.tddarchpractice.mock.TestContainer;
import com.study.tddarchpractice.user.controller.response.UserResponse;
import com.study.tddarchpractice.user.domain.UserCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserCreateControllerTest {

    private TestContainer testContainer;

    @BeforeEach
    void init() {
        testContainer = TestContainer.builder().build();
    }

    @Test
    void createUser는_유저를_생성하고_201로_응답한다() {
        // given
        UserCreate userCreate = UserCreate.builder()
                .email("oh.youngsoo23@gmail.com")
                .nickname("ohyoungsoo")
                .address("Seoul")
                .build();
        // when
        ResponseEntity<UserResponse> response = testContainer.userCreateController.createUser(userCreate);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getNickname()).isEqualTo("ohyoungsoo");
    }
}
