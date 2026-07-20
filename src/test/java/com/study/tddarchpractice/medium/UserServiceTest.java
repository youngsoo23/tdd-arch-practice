package com.study.tddarchpractice.medium;

import com.study.tddarchpractice.common.domain.exception.CertificationCodeNotMatchedException;
import com.study.tddarchpractice.common.domain.exception.ResourceNotFoundException;
import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.domain.UserCreate;
import com.study.tddarchpractice.user.domain.UserStatus;
import com.study.tddarchpractice.user.domain.UserUpdate;
import com.study.tddarchpractice.user.service.UserAuthenticationServiceImpl;
import com.study.tddarchpractice.user.service.UserCreateServiceImpl;
import com.study.tddarchpractice.user.service.UserReadServiceImpl;
import com.study.tddarchpractice.user.service.UserUpdateServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@TestPropertySource("classpath:test-application.yml")
@SqlGroup({
        @Sql(scripts = "/sql/user-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "/sql/clear-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})//SqlGroup을 사용하면 여러개의 sql을 실행할수있다. 테스트메소드가 실행되기전에 데이터를 넣는 코드와 테스트메소드가 실행된후 데이터를 지우는 코드를 작성할수있다.
public class UserServiceTest {

    @Autowired
    private UserReadServiceImpl userReadService;

    @Autowired
    private UserCreateServiceImpl userCreateService;

    @Autowired
    private UserUpdateServiceImpl userUpdateService;

    @Autowired
    private UserAuthenticationServiceImpl userAuthenticationService;

    @MockitoBean
    private JavaMailSender mailSender;

    @Test
    void getByEmail은_ACTIVE_상태의_유저를_조회할수있다() {
        // given
        String email = "oh.youngsoo23@gmail.com";
        // when
        User user = userReadService.getByEmail(email);
        // then
        assertThat(user.getNickname()).isEqualTo("ohyoungsoo");
    }

    @Test
    void getByEmail은_PENDING_상태인_유저는_찾아올수없다() {
        // given
        String email = "oh.youngsoo223@gmail.com";
        // when
        // then
        assertThatThrownBy(() -> userReadService.getByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class);
//        assertThat(userEntity.getNickname()).isEqualTo("ohyoungsoo");
    }

    @Test
    void getById는_ACTIVE_상태의_유저를_조회할수있다() {
        // given
        // when
        User user = userReadService.getById(1);
        // then
        assertThat(user.getNickname()).isEqualTo("ohyoungsoo");
    }

    @Test
    void getById는_PENDING_상태의_유저를_조회할수없다() {
        // given
        // when
        // then
        assertThatThrownBy(() -> userReadService.getById(2))
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

        BDDMockito.doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        // when
        User user = userCreateService.create(userCreate);
        // then
        assertThat((user.getId())).isNotNull();
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
    }

    @Test
    void userUpdate_를_이용한_유저_수정할수있다() {
        // given
        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("ohyoungsoo12")
                .address("Seoul Nowon")
                .build();
        // when
        userUpdateService.update(1, userUpdate);
        // then
        User user = userReadService.getById(1);
        assertThat((user.getId())).isNotNull();
        assertThat(user.getNickname()).isEqualTo("ohyoungsoo12");
        assertThat(user.getAddress()).isEqualTo("Seoul Nowon");
    }

    // 로그인 시간 비교가 어려움
    @Test
    void login_테스트_마지막_로그인시간_저장() {
        // given
        // when
        userAuthenticationService.login(1);
        // then
        User user = userReadService.getById(1);
        assertThat((user.getLastLoginAt())).isGreaterThan(0L);
//        assertThat(userEntity.getLastLoginAt()).isEqualTo("...."); //fixme: 로그인 시간 비교가 어려움
    }

    @Test
    void PENDING_상태의_유저는_이메일_인증을_통해_ACTIVE_상태로_변경할수있다() {
        // given
        // when
        userAuthenticationService.verifyEmail(2, "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        // then
        User user = userReadService.getById(2);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void PENDING_상태의_유저는_잘못된_인증코드로_인증을_시도하면_CertificationCodeNotMatchedException_발생() {
        // given
        // when
        // then
        assertThatThrownBy(() -> userAuthenticationService.verifyEmail(2, "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}
