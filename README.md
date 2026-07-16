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

## 패키지 구조

도메인별로 `controller` / `domain` / `infrastructure` / `service` 계층을 나누는 구조를 따릅니다.

```
src/main/java/com/study/tddarchpractice
├── TddArchPracticeApplication.java
├── common
│   ├── controller
│   │   ├── ExceptionControllerAdvice.java   # 전역 예외 처리
│   │   └── HealthCheckController.java       # 헬스 체크
│   └── domain/exception                     # 커스텀 예외
├── user
│   ├── controller                           # UserController, UserCreateController
│   ├── domain                               # UserCreate, UserUpdate, UserStatus
│   ├── infrastructure                       # UserEntity, UserRepository
│   └── service                              # UserService
└── post
    ├── controller                           # PostController, PostCreateController
    ├── domain                               # PostCreate, PostUpdate
    ├── infrastructure                       # PostEntity, PostRepository
    └── service                              # PostService
```

테스트 코드(`src/test/java`)도 동일한 패키지 구조를 유지하며, 계층별 단위/통합 테스트와 `@Sql`을 이용한 테스트 데이터 셋업 스크립트(`src/test/resources/sql`)를 포함합니다.

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
```

- `@SpringBootTest` 기반 통합 테스트와 `@Sql`을 통한 테스트 데이터 초기화/정리를 사용합니다.
- 외부 연동(`JavaMailSender`)은 `@MockitoBean`으로 대체하여 테스트합니다.
- 테스트 전용 설정은 `src/test/resources/test-application.yml`을 사용합니다.
