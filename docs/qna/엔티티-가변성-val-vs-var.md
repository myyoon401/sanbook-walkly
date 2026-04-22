# 엔티티 가변성 — `val` vs `var` 어떻게 정할까

> "Kotlin entity는 값 갱신이 필요하면 var로 해야 한다"는 조언을 들었는데, User 엔티티에서 비밀번호·프로필 이미지 같은 것도 전부 `var`로 해야 하는 거냐?

## 짧은 답

**아니다.** `val`/`var` 이분법보다 **"바뀔 수 있는가 + 어떻게 바뀌어야 하는가"** 로 본다.
그리고 바뀌는 필드라도 주생성자에 그냥 `var`로 열어두면 안 되고, **`var` + `protected set` + 도메인 메서드** 패턴을 쓴다.

---

## 1. 분류 기준

| 성격 | 예시 | 선언 |
|------|------|------|
| 생성 후 절대 안 바뀜 | `id`, `loginId`, `createdAt` | `val` |
| 바뀔 수 있음 | `password`, `email`, `nickname`, `profileImageUrl`, `updatedAt` | `var` (단, **`protected set`**) |

- `loginId`는 보통 불변으로 둔다 (로그인 아이디 변경은 별도 유스케이스).
- `createdAt`은 `@CreationTimestamp`로 생성 시 딱 한 번만 세팅 → `val`.
- `updatedAt`은 `@UpdateTimestamp`로 Hibernate가 갱신 → `var`.

---

## 2. 그냥 `var`로 열면 위험한 이유

```kotlin
val account = repo.findById(id)
account.password = "plaintext"   // 어디서든 가능, 해싱/정책 우회
```

Java Bean setter 그대로다. 엔티티의 가치는 "도메인 규칙을 지키는 상태 변경"에 있는데, 이러면 Bean으로 전락한다.

---

## 3. 권장 패턴 — `var` + `protected set` + 도메인 메서드

```kotlin
@Entity
class Account(
    @Id val id: UUID,

    @Column(nullable = false, unique = true)
    val loginId: String,

    password: String,              // 생성자 파라미터(통로)
    email: String,
    nickname: String,
    profileImageUrl: String?,
) {
    @Column(nullable = false)
    var password: String = password
        protected set              // 외부에서 대입 금지

    @Column(nullable = false)
    var email: String = email
        protected set

    @Column(nullable = false, unique = true)
    var nickname: String = nickname
        protected set

    @Column(nullable = true)
    var profileImageUrl: String? = profileImageUrl
        protected set

    fun changePassword(encoded: String) {
        // 해싱은 Service에서 끝내고 encoded만 받는다
        this.password = encoded
    }

    fun updateProfile(nickname: String, imageUrl: String?) {
        this.nickname = nickname
        this.profileImageUrl = imageUrl
    }
}
```

### 포인트

1. **`protected set`** — Hibernate는 프록시/리플렉션으로 필드를 세팅해야 해서 `private set`은 상황에 따라 막힐 수 있음. `protected`가 안전하다.
2. **주생성자 파라미터 (`password: String`)** — `val/var`을 안 붙이면 프로퍼티가 안 생기고, 본문의 `var password`의 초기값으로만 쓰이는 통로 역할.
3. **변경은 반드시 도메인 메서드로** — `account.password = ...` 대신 `account.changePassword(encoded)`. 엔티티가 Bean이 아닌 이유.
4. **해싱·정책 검증은 Service 책임** — 엔티티는 "유효한 값이 들어왔다"는 전제로 상태만 갱신. 엔티티 안에서 `BCrypt`를 부르지 않는다.

---

## 4. 의사결정 플로우

```
이 필드, 생성 후에 바뀔 수 있나?
├─ NO  → val
└─ YES → var + 본문에서 선언 + protected set + 도메인 메서드로만 변경
```

## 관련

- 주생성자 문법이 헷갈리면 → `docs/qna/주생성자-val-var-와-본문선언.md`
