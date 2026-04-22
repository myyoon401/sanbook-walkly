# PK 전략 — Long vs UUID, 그리고 UUID v7

> 데이터 PK는 보통 Long으로 많이 하는데, 데이터 많이 쌓이는 걸 가정하면 모든 PK가 UUID여야 합리적인가?
> Long으로 하면 데이터가 많을 때 이래저래 문제가 생기던데.

## 짧은 답

**아니다. "모든 PK = UUID"는 과하다.**
오히려 "데이터 많을수록 UUID v4는 Long보다 훨씬 불리"하다.
선택 기준은 데이터양이 아니라 **"외부에 노출되나 + 어떤 쓰기 패턴인가"**.

---

## 1. "Long이면 데이터 많을 때 문제" — 실제로 뭘 가리키나

이 표현 안엔 보통 서로 다른 3가지가 섞여 있다. 분리해서 봐야 한다.

| 흔히 말하는 "문제" | 진짜 원인 | 데이터양과 관계? |
|---|---|---|
| `/users/1`, `/users/2`로 enumeration 공격 가능 | **외부 노출** 문제 | 무관 |
| 가입자 수·주문 건수가 ID로 역추적됨 | **외부 노출** 문제 | 무관 |
| 분산 DB/샤딩에서 시퀀스 조율 병목 | **인프라** 문제 | 간접적 |
| `bigint` 범위 초과 | 거의 안 생김 (9경) | — |

정작 "데이터양"과 직결되는 성능 이슈는 **Long보다 UUID v4에서 훨씬 심각**하다.

---

## 2. UUID v4가 "대용량일수록" 나쁜 이유

### 2.1 B-Tree 인덱스 페이지 스플릿 (가장 중요)

InnoDB는 PK 순서로 디스크에 **클러스터드 인덱스**를 만든다.

- Long auto_increment → 항상 tail에 추가 → 페이지가 앞에서부터 순서대로 찬다.
- UUID v4 → 랜덤 → 매 insert가 인덱스 **중간 아무 데나** 꽂힌다.

결과: 매번 기존 페이지 쪼개기(page split), 버퍼풀 히트율 폭락, write amplification 증가.

100만 건일 땐 차이 없다. **1억 건, 10억 건일 때 수십 배 차이**로 벌어진다.

### 2.2 인덱스/스토리지 크기 2배

- Long: 8 bytes
- UUID: 16 bytes (binary), 36 bytes (문자열)

PK 자체 + 모든 FK + 모든 세컨더리 인덱스에 PK가 포함되므로 **전체 인덱스 크기 2배** → 버퍼풀 효율 반토막.

### 2.3 조인 성능

FK 조인 비교 연산이 16 vs 8 bytes. 대규모 조인에서 체감된다.

### 2.4 로그·디버깅 가독성

```
user_id=12345                                 (Long)
user_id=4f3e8a1d-9c2b-7d1e-a4f8-1b3c5d7e9f02  (UUID)
```

장애 대응 시 눈으로 ID 비교/복사할 일이 은근 많다.

---

## 3. 그럼 왜 UUID를 쓰긴 하나

Long의 진짜 약점 **"외부 노출"**을 잡기 위해서.

- `/api/users/12345` → 순차 증가 ID는 enumeration·정보 노출 취약
- URL·이메일·외부 API에 찍히는 순간 공격 표면이 된다

즉 데이터양 문제가 아니라 **보안·프라이버시** 문제. 그래서 "외부 노출되는 엔티티만 UUID"라는 하이브리드가 자연스럽다.

---

## 4. 게임 체인저 — UUID v7

v7은 상위 48비트가 **유닉스 타임스탬프(ms)**, 하위가 랜덤.

- UUID의 외부 노출 안전성 유지
- 값이 **시간순 단조 증가** → B-Tree가 Long처럼 tail에만 쌓임
- 페이지 스플릿·버퍼풀 비효율 대부분 해소

"UUID 쓰고 싶은데 B-Tree 성능 때문에 망설였다"면 v7으로 대부분 해결. 단, enumeration 저항은 v4보단 약간 약함(시간 유추 가능, 하지만 내부 랜덤 비트로 가입 순서 역추적은 불가).

- MySQL 8.x / PostgreSQL 모두 v7 생성 가능(앱 레벨 포함)
- Hibernate 6.2+ → `@UuidGenerator(style = RANDOM | TIME)` 지원

---

## 5. 실전 선택 매트릭스

| 상황 | 권장 |
|---|---|
| 외부 URL·API에 노출 + 쓰기 보통 | **UUID v7** |
| 외부 노출 + 쓰기 아주 많음(수천 TPS) | UUID v7 또는 Long PK + public_id(UUID) 분리 컬럼 |
| 순수 내부 엔티티, append-heavy (로그/측정값/원장) | **Long auto_increment** |
| 분산 DB/샤딩 환경 | UUID v7 또는 Snowflake ID |
| 로컬 단일 DB, 외부 미노출 | **Long**로 충분 |

---

## 6. walkly 적용

`docs/프로젝트-세팅-규칙.md` 5번 규칙과 그대로 일치.

| 엔티티 | 현재 | 권장 |
|---|---|---|
| `Account` | `UUID` (v4) | **UUID v7** (외부 노출) |
| `DailyStep` | `UUID` ❌ | **Long** (내부 전용 + 대량 append) |

### DailyStep을 Long으로 바꿔야 하는 이유

- 유저×일자 단위로 매일 쌓임 → 유저 10만 × 1년 = 3천6백만 건도 금방
- 전부 랜덤 UUID v4로 꽂히면 페이지 스플릿 시나리오 정면
- 외부에 `/dailySteps/{id}` 형태로 노출될 일 없음. 조회는 보통 `/users/{userId}/daily-steps?from=&to=`
- 자연 정렬(시간순)과 PK 순서가 일치해야 read/write 모두 유리

수정 예:
```kotlin
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)  // 또는 SEQUENCE
val id: Long = 0L,
```
그리고 `(userId, date)`에 유니크 인덱스를 거는 게 조회 패턴에도 맞다.

---

## 7. 한 줄 요약

> "데이터 많으면 무조건 UUID"는 틀렸다.
> **외부 노출이면 UUID v7, 내부 전용 대량 append면 Long**.
> 이 두 축으로만 고르면 대부분 끝난다.

## 관련

- 프로젝트 ID 규칙: `docs/프로젝트-세팅-규칙.md` §5
