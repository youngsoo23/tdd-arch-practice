package com.study.tddarchpractice.mock;

import com.study.tddarchpractice.common.service.port.ClockHolder;
import com.study.tddarchpractice.common.service.port.UuidHolder;
import com.study.tddarchpractice.post.controller.PostController;
import com.study.tddarchpractice.post.controller.PostCreateController;
import com.study.tddarchpractice.post.service.PostServiceImpl;
import com.study.tddarchpractice.user.controller.UserController;
import com.study.tddarchpractice.user.controller.UserCreateController;
import com.study.tddarchpractice.user.service.CertificationService;
import com.study.tddarchpractice.user.service.UserServiceImpl;
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
    public final UserServiceImpl userService;
    public final PostServiceImpl postService;

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
        this.userService = new UserServiceImpl(userRepository, certificationService, this.clockHolder, this.uuidHolder);
        this.postService = new PostServiceImpl(postRepository, userRepository);

        this.userController = new UserController(userService);
        this.userCreateController = new UserCreateController(userController, userService);
        this.postController = new PostController(postService, userController);
        this.postCreateController = new PostCreateController(postService, postController);
    }
}
