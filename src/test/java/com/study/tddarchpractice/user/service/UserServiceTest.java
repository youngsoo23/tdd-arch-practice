package com.study.tddarchpractice.user.service;

import com.study.tddarchpractice.common.domain.exception.ResourceNotFoundException;
import com.study.tddarchpractice.user.infrastructure.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@TestPropertySource("classpath:test-application.yml")
@SqlGroup({
        @Sql(scripts = "/sql/user-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "/sql/clear-user-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})//SqlGroup을 사용하면 여러개의 sql을 실행할수있다. 테스트메소드가 실행되기전에 데이터를 넣는 코드와 테스트메소드가 실행된후 데이터를 지우는 코드를 작성할수있다.
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void getByEmail은_ACTIVE_상태의_유저를_조회할수있다() {
        // given
        String email = "oh.youngsoo23@gmail.com";
        // when
        UserEntity userEntity = userService.getByEmail(email);
        // then
        assertThat(userEntity.getNickname()).isEqualTo("ohyoungsoo");
    }

    @Test
    void getByEmail은_PENDING_상태인_유저는_찾아올수없다() {
        // given
        String email = "oh.youngsoo223@gmail.com";
        // when
        // then
        assertThatThrownBy(() -> userService.getByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class);
//        assertThat(userEntity.getNickname()).isEqualTo("ohyoungsoo");
    }

    @Test
    void getById는_ACTIVE_상태의_유저를_조회할수있다() {
        // given
        // when
        UserEntity userEntity = userService.getById(1);
        // then
        assertThat(userEntity.getNickname()).isEqualTo("ohyoungsoo");
    }

    @Test
    void getById는_PENDING_상태의_유저를_조회할수없다() {
        // given
        // when
        // then
        assertThatThrownBy(() -> userService.getById(2))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
