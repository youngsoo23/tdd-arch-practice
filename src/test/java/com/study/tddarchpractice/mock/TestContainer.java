package com.study.tddarchpractice.mock;

import com.study.tddarchpractice.common.service.port.ClockHolder;
import com.study.tddarchpractice.common.service.port.UuidHolder;
import com.study.tddarchpractice.post.controller.PostController;
import com.study.tddarchpractice.post.controller.PostCreateController;
import com.study.tddarchpractice.post.service.PostCreateServiceImpl;
import com.study.tddarchpractice.post.service.PostReadServiceImpl;
import com.study.tddarchpractice.post.service.PostUpdateServiceImpl;
import com.study.tddarchpractice.user.controller.UserController;
import com.study.tddarchpractice.user.controller.UserCreateController;
import com.study.tddarchpractice.user.service.CertificationService;
import com.study.tddarchpractice.user.service.UserAuthenticationServiceImpl;
import com.study.tddarchpractice.user.service.UserCreateServiceImpl;
import com.study.tddarchpractice.user.service.UserReadServiceImpl;
import com.study.tddarchpractice.user.service.UserUpdateServiceImpl;
import lombok.Builder;

/**
 * 순수 단위 테스트에서 fake 포트와 그 fake로 조립한 서비스·컨트롤러들을 한 번에 얻기 위한 컨테이너.
 * Spring 컨텍스트 없이 {@code new TestContainer(...)} 형태로 테스트마다 새 인스턴스를 만들어 쓴다.
 */
public class TestContainer {

    public final FakeUserRepository userRepository;
    public final FakePostRepository postRepository;
    public final FakeMailSenderTest mailSender;
    public final ClockHolder clockHolder;
    public final UuidHolder uuidHolder;

    public final CertificationService certificationService;
    public final UserReadServiceImpl userReadService;
    public final UserCreateServiceImpl userCreateService;
    public final UserUpdateServiceImpl userUpdateService;
    public final UserAuthenticationServiceImpl userAuthenticationService;

    public final PostReadServiceImpl postReadService;
    public final PostCreateServiceImpl postCreateService;
    public final PostUpdateServiceImpl postUpdateService;

    public final UserController userController;
    public final UserCreateController userCreateController;
    public final PostController postController;
    public final PostCreateController postCreateController;

    @Builder
    public TestContainer(UuidHolder uuidHolder, ClockHolder clockHolder) {
        this.userRepository = new FakeUserRepository();
        this.postRepository = new FakePostRepository();
        this.mailSender = new FakeMailSenderTest();
        this.uuidHolder = uuidHolder != null ? uuidHolder : new TestUuidHolder("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        this.clockHolder = clockHolder != null ? clockHolder : new TestClockHolder(1678530673958L);

        this.certificationService = new CertificationService(mailSender);
        this.userReadService = new UserReadServiceImpl(userRepository);
        this.userCreateService = new UserCreateServiceImpl(userRepository, certificationService, this.uuidHolder);
        this.userUpdateService = new UserUpdateServiceImpl(userRepository, userReadService);
        this.userAuthenticationService = new UserAuthenticationServiceImpl(userRepository, this.clockHolder);

        this.postReadService = new PostReadServiceImpl(postRepository);
        this.postCreateService = PostCreateServiceImpl.builder()
                .postRepository(postRepository)
                .userRepository(userRepository)
                .build();
        this.postUpdateService = PostUpdateServiceImpl.builder()
                .postRepository(postRepository)
                .postReadService(postReadService)
                .build();

        this.userController = new UserController(userReadService, userUpdateService, userAuthenticationService);
        this.userCreateController = new UserCreateController(userController, userCreateService);
        this.postController = new PostController(postReadService, postUpdateService, userController);
        this.postCreateController = new PostCreateController(postCreateService, postController);
    }
}
