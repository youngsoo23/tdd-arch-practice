# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

TDD와 계층형(포트-어댑터) 아키텍처를 연습하기 위한 Spring Boot 학습용 프로젝트입니다. 회원 가입/이메일 인증, 게시물(Post) CRUD를 도메인 중심으로 구성하고, 각 계층을 테스트로 검증하며 개발합니다. 개인 연습 프로젝트로 `main` 브랜치에 직접 커밋하며 별도의 브랜치/PR 규칙은 없습니다.

## 빌드 / 실행 / 테스트

- 빌드: `./gradlew build`
- 앱 실행: `./gradlew bootRun` (기본 포트 8080, H2 파일 DB `~/mem-data`)
  - `MAIL_USERNAME`, `MAIL_APPLICATION_PASSWORD` 환경변수가 필요합니다 (Gmail SMTP 발송용). 없으면 메일 관련 기동/테스트가 실패할 수 있습니다.
- 전체 테스트: `./gradlew test`
- 단일 테스트 클래스: `./gradlew test --tests "com.study.tddarchpractice.medium.UserServiceTest"`
- 단일 테스트 메서드: `./gradlew test --tests "com.study.tddarchpractice.medium.UserServiceTest.메서드명"`
- Swagger UI: `http://localhost:8080/swagger-ui.html`, H2 콘솔: `http://localhost:8080/h2-console`, 헬스체크: `/health_check.html` (`/actuator/health` 아님)
- 린트/포매터 설정 없음(checkstyle, spotless, editorconfig 등 미존재) — 기존 코드 스타일(4-space indent)을 따르면 됩니다.

## 아키텍처: 포트-어댑터 / 의존성 역전

`user`, `post`, `common` 각 도메인 패키지는 다음 4계층으로 나뉩니다.

- `domain/*` — 프레임워크에 의존하지 않는 순수 모델. Lombok `@Builder`/`@Getter` 사용, 불변 스타일(필드는 모두 `final`, 상태 변경은 `builder()`로 새 인스턴스 생성). `ClockHolder`/`UuidHolder` 같은 포트를 필드가 아니라 메서드 파라미터로 받아 순수성을 유지합니다 (예: `User.from(UserCreate, UuidHolder)`, `User.login(ClockHolder)`).
- `service/*` — 오케스트레이션 계층. `service/port/*`에 정의된 인터페이스(아웃바운드 포트)에만 의존하고 `infrastructure` 구현체를 직접 참조하지 않습니다 (예: `UserRepository`, `MailSender`, `ClockHolder`, `UuidHolder`).
- `infrastructure/*` — 포트의 실제 구현(어댑터). Spring 스테레오타입 적용, JPA 엔티티 ↔ 도메인 모델 변환(`toModel()`/`fromModel()`) 담당 (예: `UserRepositoryImpl`, `MailSenderImpl`, `SystemClockHolder`).
- `controller/*` — Spring MVC 컨트롤러 + `response/*` DTO. 도메인별로 조회용(`XController`)과 생성용(`XCreateController`)을 분리하는 패턴을 따릅니다.

새 기능을 추가할 때도 이 계층 분리와 포트 인터페이스 패턴을 유지해 주세요.

## 테스트 컨벤션

- 테스트 메서드명은 한글로 상황을 서술하는 방식입니다 (예: `getByEmail은_ACTIVE_상태의_유저를_조회할수있다`). 새 테스트도 이 네이밍을 따릅니다.
- 각 테스트 내부에 `// given`, `// when`, `// then` 주석으로 구간을 표시합니다.
- 순수 단위 테스트는 Spring 컨텍스트 없이 서비스에 fake를 직접 주입해서 작성합니다 (`CertificationServiceTest`가 `new CertificationService(fakeMailSenderTest)` 형태로 작성된 것 참고).
- 공용 테스트 더블은 `src/test/java/com/study/tddarchpractice/mock/`에 있습니다: `FakeMailSenderTest`(이름은 `...Test`지만 `@Test` 메서드 없는 fake 구현체), `TestClockHolder`, `TestUuidHolder`. 새 포트를 추가하면 같은 위치에 같은 방식으로 fake를 추가합니다.
- Repository/통합 테스트는 `@SpringBootTest`/`@DataJpaTest` + `@Sql`(`src/test/resources/sql/*.sql`)로 데이터를 세팅하고, `JavaMailSender`만 `@MockitoBean`으로 대체합니다. 별도 프로퍼티는 `src/test/resources/test-application.yml`을 사용합니다.
- Mockito는 `build.gradle`의 `test` 태스크에서 `-javaagent`로 mockito-core를 명시적으로 주입해야 동작합니다(Spring Boot 4 / 최신 JDK 인라인 목킹 요구사항). 의존성 구성을 건드릴 때 이 설정이 깨지지 않도록 주의하세요.

## 알려진 이슈

- `./gradlew test`로 전체 테스트를 한 번에 실행하면 테스트 메서드 병렬 실행과 공유 H2 DB(`@Sql` 시드 데이터) 간 동시성 제어 문제로 실패할 수 있습니다 (`UserJpaRepositoryTest`에 관련 코멘트 있음). 알려진 이슈이며 별도 수정 요청이 없는 한 그대로 둡니다.
