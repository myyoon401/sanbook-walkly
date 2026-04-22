# 주생성자 문법 — `()` 안의 `val`/`var`, 그리고 왜 본문에서 따로 선언하는가

> 왜 어떤 필드는 `()` 안에 있고, 어떤 필드는 `{}` 안에 있지?
> `()` 안에 `val/var` 안 붙이면 그냥 `var`로 간주되는 건가?
> 왜 `@Column`이 선언부랑 본문에 쪼개져서 붙어 있지?

## 핵심 — `class Foo( ... )`의 괄호는 **주생성자**다

이 괄호 안에는 두 종류가 섞일 수 있다.

| 문법 | 뜻 |
|------|-----|
| `val x: Int` | **프로퍼티 + 생성자 파라미터** (동시에 선언) |
| `var x: Int` | **프로퍼티 + 생성자 파라미터** (가변) |
| `x: Int` (val/var 없음) | **그냥 생성자 파라미터** (프로퍼티 아님, 외부 접근 불가) |

즉 `val/var`가 붙어야 프로퍼티이고, 안 붙으면 생성자 본문에서만 쓰고 버려지는 지역 변수 같은 존재다.

---

## Q1. val/var 없으면 그냥 var로 간주되나?

**아니다. 프로퍼티 자체가 아니다.** Java로 번역해서 보자.

```kotlin
class Account(
    val id: UUID,            // (A) 프로퍼티 + 생성자 파라미터
    val loginId: String,     // (A)
    password: String,        // (B) 생성자 파라미터일 뿐
) {
    var password: String = password   // (C) 진짜 프로퍼티는 여기서 선언
        protected set
}
```

↓ Java로 번역:

```java
public class Account {
    private final UUID id;          // (A)에서 생성
    private final String loginId;   // (A)에서 생성
    private String password;        // (C)에서 생성

    public Account(UUID id, String loginId, String password /* (B) */) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;   // (B) 파라미터를 (C) 필드로 복사
    }

    public String getPassword() { return password; }
    protected void setPassword(String p) { this.password = p; }
}
```

`(B)`의 `password`는 Java 생성자 파라미터 이름에 대응할 뿐, 필드가 아니다. 필드는 `(C)`가 만든다.

---

## Q2. 왜 `@Column` 어노테이션이 선언부와 본문에 쪼개져 있나?

**분리된 게 아니라, 각 프로퍼티의 "선언 자리"에 어노테이션을 붙인 것.** 프로퍼티마다 선언 위치가 다를 뿐이다.

- `id`, `loginId`는 주생성자에 `val`이 있으니 → **거기가 선언부** → `@Column`도 거기에
- `password`, `email`은 주생성자에 `val/var`가 없어서 → 본문 `var password: ...`가 **진짜 선언부** → `@Column`도 본문에

### 그럼 왜 굳이 본문으로 빼나? — **세터 가시성 제어**

주생성자 파라미터 문법에는 `protected set`을 붙일 자리가 없다.

```kotlin
class Account(
    var password: String  // ← 여기에 "protected set" 못 붙임. 문법 불가능.
)
```

반면 본문 선언은 세터에 제한자를 붙일 수 있다.

```kotlin
var password: String = password
    protected set        // ← 외부에서 account.password = ... 금지
```

**"외부에서 대입 못 하게 막고 싶은 var"는 본문 선언으로 빼야 한다**. 그래서 생긴 분리이지, Kotlin 컨벤션이 "어노테이션을 분산시키자"는 게 아니다.

---

## 정리된 멘탈 모델

```kotlin
class Account(
    // [주생성자 영역]
    //   val/var 붙은 것 = 프로퍼티 선언 + 파라미터
    //   val/var 없는 것 = 그냥 파라미터 (본문에 값 넘겨주는 통로)

    val id: UUID,          // 불변 프로퍼티
    password: String,      // 통로 (본문의 password 초기값)

) {
    // [본문 영역]
    //   세터 제한이 필요한 var를 여기서 선언
    var password: String = password
        protected set
}
```

## 의사결정 — "언제 주생성자에서 선언, 언제 본문으로 빼는가?"

기준은 딱 하나.

> **세터 가시성 제어가 필요한가?**
> - 아니다 (`val`이거나, 아무나 세팅해도 되는 `var`) → **주생성자에서 선언**
> - 필요하다 (`protected set` / `private set`) → **본문에서 선언**

---

## 관련

- 엔티티에서 val/var 선택 기준은 → `docs/qna/엔티티-가변성-val-vs-var.md`
