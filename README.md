# 🚶 walkly

> 걸음수 랭킹으로 산책을 더 즐겁게.
> Kotlin과 Redis를 학습하기 위한 개인 프로젝트.

---

## 🎯 프로젝트 목적

**학습이 1순위, 기능이 2순위인 프로젝트.**

- **Kotlin 습득** — Java 개발자가 Kotlin을 실전 문법·이디엄 레벨까지 체득
- **Redis 활용 연습** — 걸음수 랭킹을 Sorted Set(`ZSET`)으로 구현하며 캐시/랭킹 유즈케이스 학습
- **백엔드 설계 근육 유지** — 도메인 경계, ID-only 참조, DTO 분리 등을 처음부터 신경 써서 쌓기

산책 자체를 즐겁게 만드는 게 최종 목적이고, 핵심 동기부여 장치는 **랭킹**이다. 혼자 걷는 것보다 "오늘 내 친구들 사이에서 몇 등"을 알려주면 한 번 더 나가게 된다.

---

## 🧩 주요 기능 (계획)

| 영역 | 기능 |
|------|------|
| 계정 | 회원 생성, 로그인(예정) |
| 걸음 | 일일 걸음수 기록 (Upsert) |
| 랭킹 | 오늘 / 이번 주 / 이번 달 Top N (**Redis ZSET**) |
| 산책 | 산책 세션 시작·종료, 경로 기록, 거리·시간 통계 |
| 소셜 | 친구 추가, 친구 랭킹, 주간 챌린지 |
| 기타 | 추천 산책로, 날씨 연동, 푸시 알림 |

자세한 로드맵은 [`docs/BACKLOG.md`](docs/BACKLOG.md) 참고.

---

## 🛠 기술 스택

### Core
- **Kotlin** 1.9.25 (JDK 17)
- **Spring Boot** 3.5.0
  - `spring-boot-starter-web` — REST API
  - `spring-boot-starter-data-jpa` — ORM

### 저장소
- **H2** (개발용 인메모리 DB)
- **Redis** (예정) — 랭킹 ZSET, 캐시

### 문서화
- **springdoc-openapi** 2.8.6 — Swagger UI (`/swagger-ui.html`)

### 테스트
- **JUnit 5** + `kotlin-test-junit5`

### 빌드
- **Gradle Kotlin DSL**

---

## 📂 프로젝트 구조

**Feature + Layer 하이브리드.** 도메인으로 먼저 나누고, 그 안에서 계층으로 쪼갠다.

```
src/main/kotlin/net/sanbook/walkly/
├── WalklyApplication.kt
├── account/                # 계정 도메인
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── mapper/
│   └── data/               # Request/Response DTO
├── dailystep/              # 걸음수 도메인 (미션 2)
│   └── ...
└── common/
    ├── controller/
    └── exception/
```

- 도메인 간 참조는 **ID로만** (`@ManyToOne` 금지)
- Controller → Service → Repository 계층 엄수 (테스트 코드라도 예외 없음)
- 외부 노출 API는 항상 `data class` — `Map<String, Any>` 금지

전체 규칙: [`docs/프로젝트-세팅-규칙.md`](docs/프로젝트-세팅-규칙.md)

---

## 🚀 로컬 실행

### 요구사항
- JDK 17+

### 실행
```bash
./gradlew bootRun
```

### 접속
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

---

## 📚 문서

| 문서 | 설명 |
|------|------|
| [`docs/BACKLOG.md`](docs/BACKLOG.md) | Phase별 기능 백로그 |
| [`docs/프로젝트-세팅-규칙.md`](docs/프로젝트-세팅-규칙.md) | 코드/구조/취향 규칙 (단일 진실 공급원) |
| [`docs/데일리미션/`](docs/데일리미션) | 미션별 상세 요구사항 |

---

## 📈 진행 상황

- [x] 미션 1 — Account 도메인 + 계정 생성 API
- [ ] 미션 2 — DailyStep 엔티티 + 걸음수 Upsert API
- [ ] Redis 랭킹 API
- [ ] 산책 세션 기록
- [ ] 인증(JWT)

---

## 📝 License

Personal learning project. Not licensed for redistribution.
