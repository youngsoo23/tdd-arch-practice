# 이 프로젝트의 교육적 가치

이 프로젝트(`tdd-arch-practice`)는 기능 자체(회원가입/이메일 인증, 게시물 CRUD)보다 **"어떻게 짜야 테스트 가능하고 변경에 강한 코드가 되는가"**를 연습하는 데 목적이 있습니다. 코드베이스를 실제로 분석해보면 그 이유가 구체적으로 드러납니다.

## 1. 포트-어댑터(의존성 역전) 구조를 몸으로 익힌다

`user`, `post`, `common` 각 도메인은 `domain / service / service/port / infrastructure / controller`로 계층이 분리되어 있습니다.

- `service`는 구현체가 아니라 인터페이스(`UserRepository`, `PostRepository`, `MailSender`, `ClockHolder`, `UuidHolder`)에만 의존합니다.
- `infrastructure`가 그 인터페이스를 JPA/Spring/SMTP로 구현합니다.

예를 들어 `UserService.create()`는 `UserRepositoryImpl`(JPA)을 몰라도 됩니다. 실제 DB, 실제 메일 서버 없이도 서비스 로직을 검증할 수 있다는 것이 이 구조의 실질적 이득이며, 이 프로젝트는 그 이득을 "느껴보게" 만드는 것이 핵심입니다. 실무에서 흔히 겪는 "DB/외부 API 없인 테스트를 못 짜는" 문제의 해법을 미리 연습하는 셈입니다.

## 2. 도메인 모델의 순수성 유지 연습

`User`, `Post` 같은 도메인 객체는 `ClockHolder`/`UuidHolder` 같은 포트를 필드로 들고 있지 않고, `User.from(userCreate, uuidHolder)`, `user.login(clockHolder)`처럼 **메서드 파라미터로만** 받습니다 (`src/main/java/.../user/domain/User.java`).

이 덕분에 도메인 객체는 프레임워크·시간·UUID 생성 방식에 전혀 의존하지 않는 순수 객체로 남고, 테스트에서는 `TestClockHolder(1678530673958L)`, `TestUuidHolder("aaaa...")`처럼 원하는 값을 주입해 **결정론적으로** 검증할 수 있습니다. 실제로 `medium.UserServiceTest`의 `login_테스트_마지막_로그인시간_저장` 테스트에는

```java
// 로그인 시간 비교가 어려움
assertThat(user.getLastLoginAt()).isGreaterThan(0L);
```

라는 한계가 남아 있는데, 이번에 추가한 `user.service.UserServiceTest`는 `TestClockHolder`로 시간을 고정해 `isEqualTo(1678530673958L)`로 정확히 검증합니다. **같은 로직도 어떤 테스트 더블을 쓰느냐에 따라 검증 정밀도가 달라진다**는 걸 실제 코드로 보여주는 좋은 예입니다.

## 3. Fake 기반 테스트 vs DB 기반(medium) 테스트, 두 층위를 함께 연습

이 프로젝트는 같은 서비스에 대해 두 종류의 테스트를 병행합니다.

| 구분 | 위치 | 방식 | 특징 |
| --- | --- | --- | --- |
| Fake 기반 (소형) | `user/service/UserServiceTest`, `post/service/PostServiceTest` | `new UserService(fakeRepo, ...)`로 직접 생성, Spring 컨텍스트 없음 | 빠름, 결정론적, DB/메일 서버 불필요 |
| DB 기반 (중형) | `medium/UserServiceTest`, `medium/PostServiceTest` | `@SpringBootTest` + `@Sql`로 실데이터 세팅 | 실제 JPA 매핑, 트랜잭션, 쿼리까지 검증 |

한쪽만으로는 부족합니다. Fake 테스트는 빠르지만 JPA 매핑 오류나 쿼리 문제를 못 잡고, DB 기반 테스트는 정확하지만 느리고 SQL 시드 데이터에 의존합니다(`UserJpaRepositoryTest`에 남아 있는 "병렬 실행 시 공유 H2 DB 동시성 문제"가 그 대가입니다). 이 둘을 **왜 나누어 쓰는지, 각각 무엇을 담보하는지**를 실제로 만들어보며 이해하는 것이 이 프로젝트의 핵심 학습 포인트입니다.

## 4. Mock이 아니라 Fake를 직접 구현해보는 이유

`FakeUserRepository`, `FakePostRepository`, `FakeMailSenderTest`는 Mockito로 스텁을 만드는 대신 **인터페이스를 직접 구현한 간단한 인메모리 구현체**입니다.

- Mockito 스텁은 "이 메서드가 호출되면 이 값을 리턴해"를 하나하나 지정해야 하고, 실제 동작(예: id 자동 채번, 상태 필터링)을 흉내내지 못합니다.
- Fake는 실제 저장소처럼 동작하는 축소 구현이라 `save()` 후 `findById()`로 조회하는 식의 **행동 기반 테스트**가 자연스럽습니다.

이 프로젝트에서 Mockito는 최소한으로만 쓰고(`medium` 테스트의 `JavaMailSender` 등 외부 SDK 경계), 내부 포트는 Fake로 구현하는 원칙을 지키는 것 자체가 "테스트 더블을 언제 Mock으로, 언제 Fake로 만들지"에 대한 판단력을 기르는 연습입니다.

## 5. 테스트가 코드로 남기는 설계 피드백

TDD 관점에서 이 프로젝트가 강조하는 것은 "테스트를 나중에 붙이는 것"이 아니라 **테스트하기 쉬운 구조로 먼저 설계**하는 것입니다.

- `UserService`가 `UserRepository` 인터페이스만 알기 때문에 Fake 주입이 가능해졌습니다. 만약 `UserRepositoryImpl`(JPA)에 직접 의존했다면 이런 소형 테스트 자체가 불가능했을 것입니다.
- `given/when/then` 주석과 한글 테스트명(`PENDING_상태의_유저는_잘못된_인증코드로_인증을_시도하면_CertificationCodeNotMatchedException_발생`)은 테스트 자체가 도메인 규칙의 문서 역할을 하도록 강제합니다.

즉 이 프로젝트를 연습하면서 배우는 것은 특정 프레임워크 API가 아니라, **"의존성을 어느 방향으로 두어야 테스트가 쉬워지는가"**라는, 규모가 커진 실무 프로젝트에서도 그대로 통하는 설계 감각입니다.

## 요약

| 배우는 것 | 이 코드베이스의 증거 |
| --- | --- |
| 의존성 역전 | `service/port/*` 인터페이스, `infrastructure/*` 구현체 분리 |
| 도메인 순수성 | `User`/`Post`가 포트를 필드가 아닌 파라미터로만 받음 |
| 테스트 더블 선택 | 내부 포트는 Fake(`FakeUserRepository` 등), 외부 SDK 경계는 Mock(`@MockitoBean JavaMailSender`) |
| 테스트 레벨 분리 | Fake 기반 소형 테스트 vs `@Sql` 기반 medium 테스트 |
| 결정론적 테스트 | `TestClockHolder`/`TestUuidHolder`로 시간·UUID 고정 |

이 다섯 가지는 모두 이 저장소 하나의 기능(회원가입, 게시물 CRUD)을 넘어, 규모가 큰 실무 서비스에서도 동일하게 적용되는 아키텍처/테스트 원칙입니다. 이 프로젝트가 "교육용"으로서 갖는 가치는 여기에 있습니다.
