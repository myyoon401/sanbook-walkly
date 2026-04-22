# Kotlin 기본 타입 정리

> Kotlin `Int`는 자바의 `int`인가 `Integer`인가? 그리고 Kotlin에는 어떤 기본 타입들이 있나?

## 짧은 답

- Kotlin은 **원시형/박싱형을 언어 차원에서 구분 안 한다.** `Int` 하나뿐이고, **컴파일러가 상황 봐서 `int`로 컴파일할지 `Integer`로 컴파일할지 결정**한다.
- 타입은 크게 **숫자 / 논리·문자·문자열 / 특수(Any, Unit, Nothing) / 배열 / 컬렉션 / 함수 타입**으로 나뉜다.

---

## 1. `Int` vs 자바의 `int` / `Integer`

### 매핑표

| Java | Kotlin | JVM 바이트코드 |
|---|---|---|
| `int` | `Int` (non-null, 평범한 위치) | **`int`** (원시) |
| `Integer` | `Int?` | **`Integer`** (박스) |
| `Integer` (제네릭 안) | `Int` (제네릭 인자) | **`Integer`** (박스) |

**자바의 `int`와 `Integer`를 통합해서 본 뷰**가 Kotlin의 `Int`.

### 박싱되는 규칙 3가지

1. **`?` 붙으면 박싱.** 원시는 null 못 담음. `Int?` → `Integer`.
2. **제네릭 인자면 박싱.** `List<Int>` → `List<Integer>`. JVM 제네릭 소거 때문.
3. **그 외 평범한 non-null 자리는 원시.** `fun sum(a: Int, b: Int): Int` → `int sum(int, int)`.

### 헷갈리기 쉬운 예

```kotlin
val a: Int = 1                           // int
val b: Int? = 1                          // Integer
val list: List<Int> = listOf(1, 2, 3)    // List<Integer> — 원소 전부 박싱

val x: Int? = 1
val y: Int? = 1
println(x === y)   // 참조 비교 — Integer 캐시 범위(-128~127) 영향. 쓰지 말 것.
println(x == y)    // 값 비교 — 이걸 써야 함
```

> 성능 걱정할 일은 거의 없음. **non-null로만 쓰면 자바 `int`와 동일한 원시**로 컴파일. 핫패스에서만 `Int?`, `List<Int>`, `Any로 받기` 세 가지 의식하면 됨.

---

## 2. 숫자 타입 (부호 있음)

| Kotlin | 크기 | Java 대응 | 리터럴 팁 |
|---|---|---|---|
| `Byte` | 8bit | `byte`/`Byte` | `1.toByte()` |
| `Short` | 16bit | `short`/`Short` | `1.toShort()` |
| `Int` | 32bit | `int`/`Integer` | `1` (기본) |
| `Long` | 64bit | `long`/`Long` | `1L` |
| `Float` | 32bit | `float`/`Float` | `1.0f` |
| `Double` | 64bit | `double`/`Double` | `1.0` (기본) |

- 리터럴 기본값: 정수는 `Int`, 소수는 `Double`.
- **자동 변환 없음.** `val l: Long = 1` ❌ → `1L` 혹은 `1.toLong()`.

## 3. 숫자 타입 (부호 없음)

`UByte` / `UShort` / `UInt` / `ULong`. 바이트 프로토콜, 저수준 다룰 때만. 평상시엔 안 씀.

## 4. 논리 · 문자 · 문자열

| Kotlin | Java | 비고 |
|---|---|---|
| `Boolean` | `boolean`/`Boolean` | `true`/`false` |
| `Char` | `char`/`Character` | `'A'`. **`Int`로 자동 변환 안 됨** (자바와 다름). 필요시 `.code` |
| `String` | `String` | 불변. `"""`로 멀티라인, `$var` / `${expr}`로 템플릿 |

---

## 5. 특수 타입 — `Any` / `Unit` / `Nothing`

Kotlin 타입 계층의 기둥.

### `Any`
- 모든 non-null 타입의 최상위. 자바 `Object` 대응.
- nullable 최상위는 `Any?`.
- `equals()`, `hashCode()`, `toString()` 보유.

### `Unit`
- 자바 `void` 대체. 단 **진짜 타입**(싱글톤 객체).
- 리턴 없는 함수 리턴 타입. `fun foo() { ... }` ≡ `fun foo(): Unit { ... }`.
- 타입 파라미터로 사용 가능 (`void`는 못 함). 예: `Callback<Unit>`.

### `Nothing`
- **"값이 절대 만들어지지 않는다"** 를 나타냄.
- 쓰임:
  - 항상 예외 던지는 함수 리턴: `fun fail(): Nothing = throw IllegalStateException()`
  - 영원히 안 끝나는 함수
- **모든 타입의 서브타입**. 그래서 `val x: Int = fail()` 같은 코드가 타입체크 통과.
- `Nothing?`은 null만 담는 타입 → `null` 리터럴의 실제 타입.

---

## 6. Nullable — `T?`

타입 뒤 `?` 붙이면 nullable. 별도 타입이 아니라 같은 타입의 nullable 버전.

```kotlin
val a: String = "hi"     // non-null
val b: String? = null    // nullable
```

- 자바 `@Nullable`/`@NotNull` + **컴파일 강제판**.
- `b.length` 직접 호출 불가. `b?.length`, `b!!.length`, 스마트캐스트(널 검사 후 사용) 중 택1.

---

## 7. 배열 — `Array<T>` + 원시 배열

- 제네릭: `Array<Int>`, `Array<String>` — 원소 박싱됨.
- **원시 배열 전용** (성능):

| Kotlin | Java |
|---|---|
| `IntArray` | `int[]` |
| `LongArray` | `long[]` |
| `ByteArray` | `byte[]` |
| `DoubleArray` | `double[]` |
| `BooleanArray` | `boolean[]` |
| `CharArray` | `char[]` |

핫패스면 `Array<Int>` 말고 `IntArray`.

---

## 8. 컬렉션 — 읽기/쓰기 분리

built-in은 아니고 표준 라이브러리지만 타입 개요에 함께.

| 읽기 전용 | 쓰기 가능 |
|---|---|
| `List<T>` | `MutableList<T>` |
| `Set<T>` | `MutableSet<T>` |
| `Map<K, V>` | `MutableMap<K, V>` |

- JVM에선 전부 `java.util.List/Set/Map`으로 컴파일됨. 읽기/쓰기 구분은 **Kotlin 타입 시스템 레벨의 약속**.
- 생성: `listOf()`/`setOf()`/`mapOf()` (읽기 전용), `mutableListOf()`/... (가변).
- **기본은 읽기 전용**. 필요할 때만 Mutable.

---

## 9. 함수 타입

```kotlin
val f: (Int, Int) -> Int = { a, b -> a + b }
val g: () -> Unit = { println("hi") }
val h: (String) -> String? = { null }
```

자바 `Function<A,B>`·`BiFunction` 대응. 내부적으론 arity별 인터페이스(`Function0`, `Function1`, ...)로 컴파일.

---

## 10. `Pair` / `Triple`

```kotlin
val p: Pair<String, Int> = "age" to 30
val (name, age) = "Kim" to 30   // 구조분해
```

잠깐 묶어 리턴할 때. 오래 살 거면 `data class`.

---

## 한눈 타입 계층

```
Any?          ← 모든 nullable 타입의 루트
 └─ Any       ← 모든 non-null 타입의 루트
     ├─ 숫자:  Byte/Short/Int/Long/Float/Double (+ U*)
     ├─ Boolean, Char, String
     ├─ Array<T>, IntArray 등
     ├─ 컬렉션: List/Set/Map + Mutable*
     ├─ 함수 타입: (A,B) -> C
     └─ 사용자 정의 클래스들
Unit          ← void 대체, 싱글톤 타입
Nothing       ← 모든 타입의 서브타입, 값이 없음을 의미
```

---

## 관련

- 엔티티에서 `val`/`var` 선택: `엔티티-가변성-val-vs-var.md`
- 주생성자 문법: `주생성자-val-var-와-본문선언.md`
