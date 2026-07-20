# tdd-arch-practice

TDD(Test-Driven Development)와 계층형 아키텍처를 연습하기 위한 Spring Boot 기반 학습용 프로젝트입니다. 회원 가입 · 이메일 인증 · 게시물(Post) CRUD 기능을 도메인 중심으로 구성하고, 각 계층(Controller / Service / Domain / Infrastructure)을 테스트로 검증하며 개발합니다.

## 기술 스택

- **Language**: Java 21
- **Framework**: Spring Boot 4.1.0
- **Build Tool**: Gradle
- **Database**: H2 (파일 기반, `spring.jpa.hibernate.ddl-auto: create`)
- **ORM**: Spring Data JPA / Hibernate
- **API 문서화**: springdoc-openapi (Swagger UI)
- **메일 발송**: Spring Boot Mail (Gmail SMTP)
- **기타**: Lombok, JUnit 5, Mockito, AssertJ

## 아키텍처: 포트-어댑터 / 의존성 역전

`user`, `post`, `common` 도메인 패키지는 `controller / controller/port / domain / service / service/port / infrastructure` 계층으로 나뉘며, 인바운드·아웃바운드 양방향 모두 인터페이스(포트)를 통해서만 연결됩니다.

- **인바운드 포트** (`controller/port/*`): `UserService`, `PostService` 인터페이스가 여기 위치합니다. `UserController`/`PostController`는 이 인터페이스에만 의존하고, 실제 구현체(`UserServiceImpl`, `PostServiceImpl`, 각각 `service` 패키지에 위치)를 직접 참조하지 않습니다.
- **아웃바운드 포트** (`service/port/*`): `UserRepository`, `PostRepository`, `MailSender`, `ClockHolder`, `UuidHolder` 인터페이스가 여기 위치합니다. 서비스 구현체는 이 인터페이스에만 의존하고, `infrastructure`의 실제 구현체(`UserRepositoryImpl`, `MailSenderImpl` 등)를 직접 참조하지 않습니다.
- **domain**: 프레임워크에 의존하지 않는 순수 모델(`User`, `Post`). `ClockHolder`/`UuidHolder` 같은 포트를 필드가 아니라 메서드 파라미터로 받아 순수성을 유지합니다.

이런 구조 덕분에 컨트롤러 계층조차 Spring 컨텍스트나 실제 서비스 구현 없이 `Fake*Service`를 직접 생성자에 주입해 테스트할 수 있습니다 (아래 테스트 전략의 "소형 테스트" 참고).

## 패키지 구조

```
src/main/java/com/study/tddarchpractice
├── TddArchPracticeApplication.java
├── common
│   ├── controller
│   │   ├── ExceptionControllerAdvice.java   # 전역 예외 처리
│   │   └── HealthCheckController.java       # 헬스 체크
│   ├── domain/exception                     # 커스텀 예외
│   ├── infrastructure                       # SystemClockHolder, SystemUuidHolder
│   └── service/port                         # ClockHolder, UuidHolder
├── user
│   ├── controller                           # UserController, UserCreateController
│   │   └── port                             # UserService (인바운드 포트)
│   ├── domain                               # User, UserCreate, UserUpdate, UserStatus
│   ├── infrastructure                       # UserEntity, UserJpaRepository, UserRepositoryImpl, MailSenderImpl
│   └── service                              # UserServiceImpl, CertificationService
│       └── port                             # UserRepository, MailSender (아웃바운드 포트)
└── post
    ├── controller                           # PostController, PostCreateController
    │   └── port                             # PostService (인바운드 포트)
    ├── domain                               # Post, PostCreate, PostUpdate
    ├── infrastructure                       # PostEntity, PostJpaRepository, PostRepositoryImpl
    └── service                              # PostServiceImpl
        └── port                             # PostRepository (아웃바운드 포트)
```

테스트 코드(`src/test/java`)도 동일한 패키지 구조를 유지하며, 계층별 단위/통합 테스트와 `@Sql`을 이용한 테스트 데이터 셋업 스크립트(`src/test/resources/sql`)를 포함합니다. 자세한 배경은 [`doc/why-this-project-matters.md`](doc/why-this-project-matters.md)를 참고하세요.

## 주요 기능

### User (회원)

| Method | URI | 설명 |
| --- | --- | --- |
| `POST` | `/api/users` | 회원 가입 (가입 시 `PENDING` 상태로 생성 후 인증 메일 발송) |
| `GET` | `/api/users/{id}` | 회원 단건 조회 (`ACTIVE` 상태만 조회 가능) |
| `GET` | `/api/users/{id}/verify?certificationCode=` | 이메일 인증 코드 검증 → 성공 시 `ACTIVE`로 전환 후 리다이렉트 |
| `GET` | `/api/users/me` | 내 정보 조회 (헤더 `EMAIL`, 조회 시 마지막 로그인 시각 갱신) |
| `PUT` | `/api/users/me` | 내 정보 수정 (닉네임/주소) |

- 회원 상태(`UserStatus`)는 `PENDING → ACTIVE`로 전환되며, 인증 코드가 일치하지 않으면 `CertificationCodeNotMatchedException`(403)이 발생합니다.
- 존재하지 않는 리소스 조회 시 `ResourceNotFoundException`(404)이 발생합니다.

### Post (게시물)

| Method | URI | 설명 |
| --- | --- | --- |
| `POST` | `/api/posts` | 게시물 작성 (작성자 ID 기반으로 유저 조회 후 연관관계 설정) |
| `GET` | `/api/posts/{id}` | 게시물 단건 조회 |
| `PUT` | `/api/posts/{id}` | 게시물 내용 수정 |

### 공통

- `GET /health_check.html` : 헬스 체크
- `ExceptionControllerAdvice`를 통해 도메인 예외를 HTTP 상태 코드로 매핑

## 실행 방법

### 사전 요구사항

- JDK 21
- (이메일 인증 기능 사용 시) Gmail SMTP 계정 및 앱 비밀번호

### 환경 변수

이메일 발송 기능을 사용하려면 아래 환경 변수가 필요합니다.

```bash
export MAIL_USERNAME=your_gmail_address
export MAIL_APPLICATION_PASSWORD=your_gmail_app_password
```

### 애플리케이션 실행

```bash
./gradlew bootRun
```

기본적으로 `8080` 포트에서 실행되며, H2 파일 DB(`~/mem-data`)를 사용합니다.

- H2 Console: http://localhost:8080/h2-console
- Swagger UI: http://localhost:8080/swagger-ui.html

## 테스트

```bash
./gradlew test
# 단일 클래스
./gradlew test --tests "com.study.tddarchpractice.medium.UserServiceTest"
# 단일 메서드
./gradlew test --tests "com.study.tddarchpractice.medium.UserServiceTest.메서드명"
```

이 프로젝트는 세 층위의 테스트를 함께 연습합니다.

| 층위 | 위치 | 방식 | 특징 |
| --- | --- | --- | --- |
| 도메인 (가장 빠름) | `user/domain`, `post/domain` | `builder()`로 객체를 직접 생성해 순수 메서드만 검증 | Spring/DB 전혀 불필요, 밀리초 단위 |
| 소형 (Fake 기반) | `user/service`, `post/service`, `user/controller`, `post/controller` | 서비스/컨트롤러를 생성자에 `Fake*Repository`, `Fake*Service` 등을 직접 주입해 생성, Spring 컨텍스트 없음 | 빠르고 결정론적, DB/메일 서버 불필요 |
| 중형 (통합) | `medium/*` | `@SpringBootTest` + `@Sql`로 실데이터 세팅, `MockMvc`로 HTTP 계층까지 검증 | 실제 JPA 매핑·트랜잭션·쿼리까지 검증 |

- 공용 Fake/테스트 더블은 `src/test/java/com/study/tddarchpractice/mock/`에 모여 있습니다: `FakeUserRepository`, `FakePostRepository`, `FakeMailSenderTest`, `TestClockHolder`, `TestUuidHolder`. 이들을 조합해 실제 `UserServiceImpl`/`PostServiceImpl`/컨트롤러까지 한 번에 조립해주는 `TestContainer`도 같은 위치에 있습니다.
- 소형 테스트는 컨트롤러/서비스 인터페이스(포트)에만 의존하도록 설계되어 있기 때문에 실제 구현체(JPA, SMTP) 없이도 `TestContainer.builder().build()` 한 줄로 테스트가 가능합니다.
- 중형 테스트는 외부 연동(`JavaMailSender`)만 `@MockitoBean`으로 대체하고, 나머지는 실제 구성으로 띄워 검증합니다.
- 테스트 전용 설정은 `src/test/resources/test-application.yml`을 사용합니다.
- Mockito는 `build.gradle`의 `test` 태스크에서 `-javaagent`로 mockito-core를 명시적으로 주입해야 동작합니다(Spring Boot 4 / 최신 JDK 인라인 목킹 요구사항).

### 알려진 이슈

`./gradlew test`로 전체 테스트를 한 번에 실행하면 테스트 메서드 병렬 실행과 공유 H2 DB(`@Sql` 시드 데이터) 간 동시성 제어 문제로 실패할 수 있습니다 (`UserJpaRepositoryTest`에 관련 코멘트 있음).
