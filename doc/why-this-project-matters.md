# 이 프로젝트의 교육적 가치

이 프로젝트(`tdd-arch-practice`)는 기능 자체(회원가입/이메일 인증, 게시물 CRUD)보다 **"어떻게 짜야 테스트 가능하고 변경에 강한 코드가 되는가"**를 연습하는 데 목적이 있습니다. 코드베이스를 실제로 분석해보면 그 이유가 구체적으로 드러납니다.

## 1. 포트-어댑터(의존성 역전) 구조를 몸으로 익힌다

`user`, `post`, `common` 각 도메인은 `domain / controller/port / service / service/port / infrastructure / controller`로 계층이 분리되어 있습니다.

- `service`는 구현체가 아니라 인터페이스(`UserRepository`, `PostRepository`, `MailSender`, `ClockHolder`, `UuidHolder`)에만 의존합니다.
- `infrastructure`가 그 인터페이스를 JPA/Spring/SMTP로 구현합니다.
- `controller`는 `controller/port`의 `UserService`/`PostService` 인터페이스에만 의존하고, 실제 구현체(`UserServiceImpl`, `PostServiceImpl`)를 직접 참조하지 않습니다.

예를 들어 `UserController`는 `UserServiceImpl`이 내부적으로 `UserRepositoryImpl`(JPA)을 쓰는지, 어떻게 이메일을 보내는지 몰라도 됩니다. 실제 DB, 실제 메일 서버 없이도 서비스 로직을 검증할 수 있다는 것이 이 구조의 실질적 이득이며, 이 프로젝트는 그 이득을 "느껴보게" 만드는 것이 핵심입니다. 실무에서 흔히 겪는 "DB/외부 API 없인 테스트를 못 짜는" 문제의 해법을 미리 연습하는 셈입니다.

## 2. 도메인 모델의 순수성 유지 연습

`User`, `Post` 같은 도메인 객체는 `ClockHolder`/`UuidHolder` 같은 포트를 필드로 들고 있지 않고, `User.from(userCreate, uuidHolder)`, `user.login(clockHolder)`처럼 **메서드 파라미터로만** 받습니다 (`src/main/java/.../user/domain/User.java`).

이 덕분에 도메인 객체는 프레임워크·시간·UUID 생성 방식에 전혀 의존하지 않는 순수 객체로 남고, 테스트에서는 `TestClockHolder(1678530673958L)`, `TestUuidHolder("aaaa...")`처럼 원하는 값을 주입해 **결정론적으로** 검증할 수 있습니다. 실제로 `medium.UserServiceTest`의 `login_테스트_마지막_로그인시간_저장` 테스트에는

```java
// 로그인 시간 비교가 어려움
assertThat(user.getLastLoginAt()).isGreaterThan(0L);
```

라는 한계가 남아 있는데, `user.service.UserServiceTest`(Fake 기반 소형 테스트)는 `TestClockHolder`로 시간을 고정해 `isEqualTo(1678530673958L)`로 정확히 검증합니다. **같은 로직도 어떤 테스트 더블을 쓰느냐에 따라 검증 정밀도가 달라진다**는 걸 실제 코드로 보여주는 좋은 예입니다.

## 3. Fake 기반 테스트 vs DB 기반(medium) 테스트, 두 층위를 함께 연습

이 프로젝트는 같은 서비스에 대해 두 종류의 테스트를 병행합니다.

| 구분 | 위치 | 방식 | 특징 |
| --- | --- | --- | --- |
| Fake 기반 (소형) | `user/service`, `post/service`, `user/controller`, `post/controller` | `TestContainer.builder().build()`로 fake 저장소 + 실제 서비스/컨트롤러를 조립, Spring 컨텍스트 없음 | 빠름, 결정론적, DB/메일 서버 불필요 |
| DB 기반 (중형) | `medium/*` | `@SpringBootTest` + `@Sql`로 실데이터 세팅, `@Autowired`로 실제 스프링 빈 주입 | 실제 JPA 매핑, 트랜잭션, 쿼리, MockMvc를 통한 HTTP 계층까지 검증 |

한쪽만으로는 부족합니다. Fake 테스트는 빠르지만 JPA 매핑 오류나 쿼리 문제를 못 잡고, DB 기반 테스트는 정확하지만 느리고 SQL 시드 데이터에 의존합니다(`UserJpaRepositoryTest`에 남아 있는 "병렬 실행 시 공유 H2 DB 동시성 문제"가 그 대가입니다). 이 둘을 **왜 나누어 쓰는지, 각각 무엇을 담보하는지**를 실제로 만들어보며 이해하는 것이 이 프로젝트의 핵심 학습 포인트입니다.

## 4. Mock이 아니라 Fake를 직접 구현해보는 이유

`FakeUserRepository`, `FakePostRepository`, `FakeMailSenderTest`는 Mockito로 스텁을 만드는 대신 **인터페이스를 직접 구현한 간단한 인메모리 구현체**입니다.

- Mockito 스텁은 "이 메서드가 호출되면 이 값을 리턴해"를 하나하나 지정해야 하고, 실제 동작(예: id 자동 채번, 상태 필터링)을 흉내내지 못합니다.
- Fake는 실제 저장소처럼 동작하는 축소 구현이라 `save()` 후 `findById()`로 조회하는 식의 **행동 기반 테스트**가 자연스럽습니다.

이 프로젝트에서 Mockito는 최소한으로만 쓰고(`medium` 테스트의 `JavaMailSender` 등 외부 SDK 경계), 내부 포트는 Fake로 구현하는 원칙을 지키는 것 자체가 "테스트 더블을 언제 Mock으로, 언제 Fake로 만들지"에 대한 판단력을 기르는 연습입니다.

## 5. `TestContainer`: Fake 조립을 테스트 클래스 밖으로 꺼내기

처음에는 `UserServiceTest`, `PostServiceTest`, `UserControllerTest` 등 각 테스트 클래스의 `@BeforeEach`가 다음처럼 fake와 서비스를 매번 손으로 조립했습니다.

```java
fakeUserRepository = new FakeUserRepository();
fakeMailSenderTest = new FakeMailSenderTest();
CertificationService certificationService = new CertificationService(fakeMailSenderTest);
userService = new UserServiceImpl(fakeUserRepository, certificationService, ...);
userController = new UserController(userService);
```

테스트 클래스가 늘어날수록 이 와이어링 코드가 그대로 복제됐습니다. 이를 `src/test/java/.../mock/TestContainer.java` 하나로 모아서, `TestContainer.builder().build()` 호출 한 줄이 fake 저장소부터 서비스, 컨트롤러까지 전부 조립한 객체 그래프를 돌려주도록 바꿨습니다.

```java
testContainer = TestContainer.builder().build();
testContainer.userRepository.save(User.builder()...build());
// ...
testContainer.userController.getUserById(1);
```

`TestContainer`는 Spring의 `ApplicationContext`가 하는 일(빈 그래프 조립)을 아주 작은 스코프에서 손으로 흉내낸 것입니다. Spring 컨테이너를 띄우지 않고도 "이 테스트에 필요한 객체들이 서로 올바르게 연결돼 있는 상태"를 재사용 가능한 형태로 얻을 수 있다는 점에서, 왜 DI 컨테이너가 유용한지를 거꾸로 체감하게 해주는 연습이기도 합니다.

## 6. 유스케이스별로 포트를 쪼갰다가 다시 합친 이유 (SRP/ISP vs 실용성, 그리고 OCP)

이 프로젝트는 한때 `UserService`를 `UserReadService`/`UserCreateService`/`UserUpdateService`/`UserAuthenticationService`로, `PostService`를 `PostReadService`/`PostCreateService`/`PostUpdateService`로 쪼갠 적이 있습니다. 유스케이스 하나당 인터페이스 하나, 구현체 하나를 두는 전형적인 SRP(단일 책임 원칙)/ISP(인터페이스 분리 원칙) 적용이었습니다.

이 구조를 다시 하나의 `UserService`/`PostService`로 합치면서 실제로 겪은 두 가지가 있습니다.

**(1) 인터페이스를 `extends`로 합치면 Spring이 헷갈려한다.** 처음엔 `UserService extends UserReadService, UserCreateService, ...`처럼 만들고 `UserServiceImpl`이 내부 4개 서비스를 주입받아 위임하는 파사드로 구현했습니다. 그런데 `UserServiceImpl`이 `UserReadService` 타입도 되어버리기 때문에, `UserUpdateServiceImpl`처럼 다른 곳에서 `UserReadService`를 주입받으려는 지점에서 후보 빈이 2개가 되어 `NoUniqueBeanDefinitionException`이 날 수 있는 구조가 됩니다. `@Primary`로 우회할 수는 있지만, 애초에 `extends` 없이 메서드를 인터페이스에 직접 나열하는 편이 더 단순하고 이런 함정 자체가 생기지 않습니다.

**(2) "유스케이스별 분리"가 항상 이득은 아니다.** 세분화된 구조에서는 컨트롤러가 필요한 만큼만 정확히 주입받을 수 있고(ISP), 새 유스케이스를 추가할 때 기존 클래스를 건드리지 않고 새 구현체만 추가할 수 있다는 점에서 OCP(개방-폐쇄 원칙)에 유리합니다. 하지만 이 프로젝트 규모에서는 그 이득보다 "컨트롤러마다 여러 포트를 주입받아야 하는 번거로움"과 "클래스 수 증가"라는 비용이 더 크게 느껴졌습니다. 그래서 다시 `UserService`/`PostService` 하나로 합치고, `UserServiceImpl`/`PostServiceImpl`이 위임 없이 `UserRepository`/`CertificationService`/`ClockHolder`/`UuidHolder`를 직접 써서 로직을 구현하도록 되돌렸습니다.

이 왕복 자체가 배울 점입니다: SRP/ISP/OCP 같은 원칙은 무조건 잘게 쪼갤수록 좋다는 뜻이 아니라, **지금 이 코드베이스의 규모와 변경 빈도에서 어느 쪽이 더 싼가**를 저울질하는 도구입니다. 이 프로젝트처럼 작은 규모에서는 하나로 합친 인터페이스가 더 읽기 쉽고 유지비가 낮을 수 있고, 유스케이스가 독립적으로 자주 확장되는 큰 서비스에서는 세분화가 더 유리할 수 있습니다. "원칙을 지켰다/어겼다"보다 "지금 상황에서 어떤 트레이드오프를 선택했는가"를 설명할 수 있는 게 더 중요합니다.

## 7. 테스트가 코드로 남기는 설계 피드백

TDD 관점에서 이 프로젝트가 강조하는 것은 "테스트를 나중에 붙이는 것"이 아니라 **테스트하기 쉬운 구조로 먼저 설계**하는 것입니다.

- `UserServiceImpl`이 `UserRepository` 인터페이스만 알기 때문에 `TestContainer`를 통한 Fake 주입이 가능해졌습니다. 만약 `UserRepositoryImpl`(JPA)에 직접 의존했다면 이런 소형 테스트 자체가 불가능했을 것입니다.
- `given/when/then` 주석과 한글 테스트명(`PENDING_상태의_유저는_잘못된_인증코드로_인증을_시도하면_CertificationCodeNotMatchedException_발생`)은 테스트 자체가 도메인 규칙의 문서 역할을 하도록 강제합니다.

즉 이 프로젝트를 연습하면서 배우는 것은 특정 프레임워크 API가 아니라, **"의존성을 어느 방향으로 두어야 테스트가 쉬워지는가"**라는, 규모가 커진 실무 프로젝트에서도 그대로 통하는 설계 감각입니다.

## 8. `PostTest`/`UserTest`: H2/DB, Spring 없이도 도메인 규칙을 검증한다

`src/test/java/.../post/domain/PostTest.java`, `.../user/domain/UserTest.java`는 `@SpringBootTest`도 `@DataJpaTest`도, H2 DB도 전혀 필요로 하지 않습니다. Spring 컨텍스트를 띄우지 않고 `Post`/`User`/`PostCreate`/`PostUpdate` 객체를 `builder()`로 직접 만든 뒤, `Post.from(...)`/`post.update(...)` 같은 순수 메서드 호출 결과만 `assertThat`으로 검증합니다.

이게 가능한 이유는 2번에서 설명한 "도메인 순수성" 덕분입니다. `Post`/`User`는 각각 `PostEntity`/`UserEntity`(JPA)나 리포지토리를 전혀 몰라도 생성/수정 로직을 스스로 완결하도록 설계돼 있기 때문에, DB나 프레임워크를 한 번도 거치지 않고도 도메인 규칙(수정 시 `id`/`createdAt`/`writer`는 유지되고 `content`/`modifiedAt`만 바뀐다 등)을 검증할 수 있습니다.

### 왜 중요한가

1. **속도** — H2조차 띄우지 않으므로 JVM이 객체를 생성하고 메서드를 호출하는 수준의 속도로 실행됩니다. `medium` 패키지의 `@SpringBootTest` 테스트는 스프링 컨텍스트 로딩에만 수 초가 걸리는데, 이런 도메인 테스트는 밀리초 단위입니다. 테스트가 많아질수록 이 차이가 누적되어 TDD가 요구하는 "빠른 피드백 루프"를 실제로 가능하게 합니다.
2. **결정론(Flaky 하지 않음)** — H2/DB 기반 테스트는 `@Sql` 시드 데이터, 트랜잭션 롤백 타이밍, (이 프로젝트에 실제로 알려진 이슈로 남아 있는) 병렬 실행 시 공유 DB 동시성 문제 같은 외부 변수에 노출됩니다. 순수 도메인 테스트는 오직 입력값과 메서드 로직만으로 결과가 결정되므로 몇 번을 돌려도 같은 결과가 나옵니다.
3. **실패 원인의 국소화** — DB까지 관여하는 테스트가 실패하면 "도메인 로직이 틀렸는지, JPA 매핑이 틀렸는지, 시드 데이터가 틀렸는지"를 가려내야 합니다. `PostTest`처럼 도메인 계층만 격리해 테스트하면 실패 시 원인이 곧바로 `Post`/`PostUpdate`의 로직으로 좁혀집니다.
4. **설계가 깨지지 않았다는 증거** — 이런 테스트를 "가볍게 쓸 수 있다"는 사실 자체가 `Post`가 인프라(H2, JPA, Spring)에 의존하지 않게 설계돼 있다는 것을 보여주는 리트머스 시험지입니다. 나중에 누군가 `Post` 안에 `PostRepository`나 `EntityManager`를 끌어들이면 이런 테스트를 더 이상 가볍게 작성할 수 없게 되고, 그 자체가 설계가 잘못된 방향으로 가고 있다는 신호가 됩니다.
5. **테스트 피라미드의 가장 아래층** — 3번에서 다룬 Fake 기반(소형) vs DB 기반(medium) 테스트보다 한 단계 더 아래, 가장 빠르고 가장 많아야 할 계층이 바로 이 도메인 단위 테스트입니다. 이 계층이 두꺼울수록 위쪽(서비스/통합) 테스트는 "각 계층이 서로 잘 협력하는지"만 검증하면 되므로 전체 테스트 스위트가 가벼워집니다.

## 요약

| 배우는 것 | 이 코드베이스의 증거 |
| --- | --- |
| 의존성 역전 | `controller/port/*` 인바운드 포트, `service/port/*` 아웃바운드 포트, `infrastructure/*` 구현체 분리 |
| 도메인 순수성 | `User`/`Post`가 포트를 필드가 아닌 파라미터로만 받음 |
| 테스트 더블 선택 | 내부 포트는 Fake(`FakeUserRepository` 등), 외부 SDK 경계는 Mock(`@MockitoBean JavaMailSender`) |
| 테스트 조립의 재사용 | `TestContainer`가 fake 저장소부터 서비스·컨트롤러까지 한 번에 조립 |
| 테스트 레벨 분리 | Fake 기반 소형 테스트 vs `@Sql` 기반 medium 테스트 |
| 결정론적 테스트 | `TestClockHolder`/`TestUuidHolder`로 시간·UUID 고정 |
| 원칙은 저울질의 도구 | 유스케이스별 포트 분리(SRP/ISP/OCP) ↔ 단일 포트 통합, 규모에 맞는 트레이드오프 선택 |
| H2/Spring 없는 도메인 테스트 | `PostTest`/`UserTest` — 도메인 로직만 순수 객체로 검증 |

이 여덟 가지는 모두 이 저장소 하나의 기능(회원가입, 게시물 CRUD)을 넘어, 규모가 큰 실무 서비스에서도 동일하게 적용되는 아키텍처/테스트 원칙입니다. 이 프로젝트가 "교육용"으로서 갖는 가치는 여기에 있습니다.
