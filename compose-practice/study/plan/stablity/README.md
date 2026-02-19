# Compose Stability 학습

Compose 리컴포지션 최적화의 핵심인 Stability 시스템을 학습합니다.

- Test Tool: Espresso, JUnit4 (androidTest)
- 공식 문서: https://developer.android.com/develop/ui/compose/performance/stability

---

## 학습 내용

### 1. Stable vs Immutable (`StabilityTest.kt`)

Compose가 리컴포지션을 스킵할 수 있는 조건을 학습합니다.

**파라미터 비교 순서:**
1. `===` (참조 비교) — 같은 객체면 리컴포지션 스킵
2. 참조가 다르면 어노테이션에 따라 다르게 처리:
   - `@Stable` → `equals()` 비교 후 같으면 스킵
   - `@Immutable` → 비교 없이 무조건 불변으로 간주

```kotlin
// @Stable: equals()가 true면 리컴포지션 스킵
@Stable
data class User(val name: String)

// @Immutable: 내부 값이 절대 변하지 않음을 보장
@Immutable
data class Config(val theme: String, val locale: String)
```

**핵심 차이:**

| | `@Stable` | `@Immutable` |
|--|--|--|
| equals() 비교 | 수행함 | 수행 안 함 |
| 내부 변경 | 허용 (개발자 책임) | 금지 |
| 리컴포지션 스킵 | equals() true 시 | 항상 |

**학습 파일:** `stable.md`, `stable-vs-immutable.md`

---

### 2. Compose Group (`compose-group.md`)

Compose 컴파일러가 코드를 그룹으로 나누는 방식을 학습합니다.

---

## 다음 학습 (TODO)

- [ ] `inline` vs `noinline` — 람다 파라미터 최적화
- [ ] Stable/Immutable 테스트 코드로 직접 비교 검증
- [ ] SideEffect 공부 (`LaunchedEffect`, `SideEffect`, `DisposableEffect`)

---

## 참고 링크

- [Compose Stability 공식 문서](https://developer.android.com/develop/ui/compose/performance/stability)
- [Compose Stability Explained](https://developer.android.com/develop/ui/compose/performance/stability/diagnose)
