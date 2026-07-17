---
name: new-port-adapter
description: 이 프로젝트의 포트-어댑터(의존성 역전) 패턴에 맞춰 새 포트 인터페이스, 어댑터 구현체, fake 테스트 더블을 함께 스캐폴딩합니다. "OO 기능을 위한 포트를 추가해줘", "새 어댑터 만들어줘" 같은 요청에 사용하세요.
---

# 포트-어댑터 스캐폴딩

이 저장소는 `service` 계층이 인터페이스(포트)에만 의존하고, `infrastructure` 계층이 그 포트를 구현하는 의존성 역전 패턴을 연습하는 프로젝트입니다. 새 외부 의존성(메일 발송, 시계, UUID 생성, 저장소 등)이 필요할 때 아래 순서로 3개 파일을 함께 만듭니다.

기존 예시: `common/service/port/ClockHolder` ↔ `common/infrastructure/SystemClockHolder` ↔ `test/.../mock/TestClockHolder`, `user/service/port/MailSender` ↔ `user/infrastructure/MailSenderImpl` ↔ `test/.../mock/FakeMailSenderTest`.

## 절차

1. **포트 인터페이스** 정의: `<domain>/service/port/<Name>.java`. 서비스 계층이 실제로 필요로 하는 최소한의 메서드만 선언합니다. 프레임워크 의존성이 없어야 합니다.
2. **실제 구현(어댑터)** 작성: `<domain>/infrastructure/<Name>Impl.java` (또는 `System<Name>`처럼 기존 네이밍 패턴을 따름). Spring 스테레오타입(`@Component` 등)을 붙이고, 필요하면 외부 라이브러리(JavaMailSender 등)를 감쌉니다.
3. **fake 테스트 더블** 작성: `src/test/java/com/study/tddarchpractice/mock/<Name의 fake>.java`. Mockito가 아니라 손으로 작성한 fake로, 포트 인터페이스를 구현하고 테스트에서 결정론적 동작을 보장합니다. 기존 파일들(`TestClockHolder`, `TestUuidHolder`, `FakeMailSenderTest`)의 네이밍/스타일을 따르세요.
4. **서비스에서 사용**: 서비스 클래스는 생성자로 포트 인터페이스를 주입받습니다(`@RequiredArgsConstructor` + `private final` 필드). `infrastructure` 구현체를 직접 참조하지 않습니다.
5. **단위 테스트 작성**: Spring 컨텍스트 없이 `new XxxService(fakeXxx)` 형태로 직접 생성해서 테스트합니다(`CertificationServiceTest` 참고). 테스트 메서드명은 한글로, `// given` / `// when` / `// then` 주석 구간을 사용합니다.

## 체크리스트

- [ ] 포트 인터페이스가 `service/port/`에 있고 프레임워크 의존성이 없는가
- [ ] 어댑터가 `infrastructure/`에 있고 포트를 구현하는가
- [ ] fake 더블이 `test/.../mock/`에 있고 포트를 구현하는가
- [ ] 서비스가 포트 인터페이스에만 의존하는가 (어댑터 직접 참조 없음)
- [ ] 단위 테스트가 fake를 사용해 Spring 컨텍스트 없이 동작하는가
