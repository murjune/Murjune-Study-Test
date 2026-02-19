# Flow 조합 연산자 비교: Combine vs Zip vs Merge

## 개요

여러 Flow를 조합하는 세 가지 주요 연산자의 차이점과 사용 사례를 정리한 문서입니다.

## 1. Combine 연산자

### 특징
- **조합 방식**: 모든 Upstream Flow의 **최신 값**을 조합하여 방출
- **방출 조건**:
  - 첫 방출: 모든 Flow가 최소 한 번씩 값을 방출해야 함
  - 이후: 어느 Flow든 값이 변경되면 최신 값들을 조합해서 방출
- **독립성**: 각 Flow는 독립적으로 동작 (Blocking 없음)
- **수식**: `1 + 1 -> 여러 개` (최신 값을 계속 조합)

### 예시
```kotlin
val flow1 = flowOf(1, 2, 3).onEach { delay(20) }
val flow2 = flowOf("a", "b", "c").onEach { delay(31) }

combine(flow1, flow2) { a, b -> "$a$b" }
// 결과: "1a", "2a", "3a", "3b", "3c"
```

### 타임라인
```
20ms   40ms   60ms   62ms   93ms
  1      2      3
       a            b            c
결과: 1a    2a     3a     3b     3c
```

### 주요 특징
- ✅ 여러 상태를 조합해서 UI를 업데이트할 때 유용
- ✅ 각 Flow가 독립적으로 방출 가능
- ⚠️ 모든 Flow가 첫 번째 값을 방출해야 combine이 시작됨
- 💡 `onStart { emit(초기값) }`으로 초기 방출 보장 가능

### 사용 사례
```kotlin
// ViewModel에서 여러 상태를 조합
combine(
    userFlow,
    settingsFlow,
    permissionFlow
) { user, settings, permission ->
    UiState(user, settings, permission)
}
```

---

## 2. Zip 연산자

### 특징
- **조합 방식**: 두 Flow의 값을 **1:1로 쌍**을 맞춰서 방출
- **방출 조건**: 양쪽 Flow에서 각각 하나씩 값이 있어야 쌍을 만들어 방출
- **독립성**: 한쪽 Flow가 방출될 때까지 다른 Flow는 **대기** (Blocking)
- **수식**: `1 + 1 -> 1` (쌍으로 묶음)
- **특징**: 운명 공동체 - 한쪽이 느리면 전체가 느려짐

### 예시
```kotlin
val flow1 = flowOf(1, 2, 3)
val flow2 = flowOf("a", "b", "c", "d")

flow1.zip(flow2) { a, b -> "$a$b" }
// 결과: "1a", "2b", "3c"
// "d"는 짝이 없어서 무시됨
```

### 타임라인
```
flow1:  1      2       3
flow2:  a      b       c      d(대기 후 무시)
결과:   1a     2b      3c
```

### 주요 특징
- ⚠️ 한쪽 Flow가 느리면 다른 Flow도 대기 (Blocking)
- ⚠️ 짧은 쪽 Flow에 맞춰서 방출 (남은 값은 무시)
- 🚨 각 Flow에서 값을 **하나씩만 소비**
- 💡 Combine과 달리 "최신 값"이 아닌 "순서대로 쌍"

### 사용 사례
```kotlin
// 두 개의 Flow를 순서대로 쌍을 맞춰 처리
val requests = flowOf(req1, req2, req3)
val responses = flowOf(res1, res2, res3)

requests.zip(responses) { request, response ->
    ProcessedData(request, response)
}
```

---

## 3. Merge 연산자

### 특징
- **조합 방식**: 여러 Flow를 하나로 합쳐서 **방출 순서대로** 내보냄
- **방출 조건**: 각 Flow가 값을 방출하는 즉시 전달
- **독립성**: 각 Flow는 완전히 독립적으로 방출 (Blocking 없음)
- **수식**: `1 + 1 -> 2` (단순 합침)

### 예시
```kotlin
val flow1 = flowOf(1, 2, 3).onEach { delay(7) }
val flow2 = flowOf(4, 5, 6).onEach { delay(15) }

merge(flow1, flow2)
// 결과: 1, 2, 4, 3, 5, 6
```

### 타임라인
```
7ms   14ms   15ms   21ms   30ms   45ms
 1      2      4      3      5      6
```

### 주요 특징
- ✅ 각 Flow가 완전히 독립적으로 방출
- ✅ 하나의 Flow가 느려도 다른 Flow 방출에 영향 없음
- ✅ 여러 이벤트 소스를 하나로 합칠 때 유용
- ❌ 다른 타입의 Flow를 합치면 `Any` 타입이 되므로 주의

### 사용 사례
```kotlin
// 여러 UI 이벤트를 하나의 Flow로 합침
sealed interface UiEvent {
    object NavigateToDetail : UiEvent
    object NavigateToHome : UiEvent
    object ShowToast : UiEvent
}

val buttonClickEvents: Flow<UiEvent>
val swipeEvents: Flow<UiEvent>
val menuEvents: Flow<UiEvent>

merge(buttonClickEvents, swipeEvents, menuEvents)
    .collect { event ->
        handleUiEvent(event)
    }
```

---

## 비교 표

| 특징 | Combine | Zip | Merge |
|-----|---------|-----|-------|
| **조합 방식** | 최신 값 조합 | 1:1 쌍 매칭 | 순서대로 합침 |
| **방출 개수** | 1+1 -> 여러 개 | 1+1 -> 1 | 1+1 -> 2 |
| **Blocking** | ❌ 독립적 | ✅ 쌍 대기 | ❌ 독립적 |
| **첫 방출 조건** | 모든 Flow 1회 필요 | 양쪽 1개씩 필요 | 즉시 방출 |
| **길이 차이** | 긴 쪽 기준 | 짧은 쪽 기준 | 모두 방출 |
| **값 재사용** | ✅ 최신 값 재사용 | ❌ 한 번만 사용 | - |
| **타입** | 조합 타입 | 조합 타입 | 같은 타입 권장 |

## 선택 가이드

### Combine을 사용하는 경우
- ✅ 여러 상태를 조합해서 UI 업데이트
- ✅ 각 상태가 독립적으로 변경됨
- ✅ 항상 최신 값들의 조합이 필요

```kotlin
// ViewModel 상태 조합
combine(userState, settingsState, permissionState) { ... }
```

### Zip을 사용하는 경우
- ✅ 두 Flow를 순서대로 1:1 매칭
- ✅ 각 값을 한 번씩만 소비해야 함
- ✅ Request-Response 패턴

```kotlin
// API 요청과 응답 매칭
requests.zip(responses) { req, res -> ... }
```

### Merge를 사용하는 경우
- ✅ 여러 이벤트 소스를 하나로 통합
- ✅ 각 이벤트가 독립적으로 발생
- ✅ 모든 이벤트를 순서대로 처리

```kotlin
// UI 이벤트 통합
merge(clickEvents, swipeEvents, longPressEvents)
```

## 주의사항

### Combine
```kotlin
// ❌ 한쪽 Flow가 empty면 아무것도 방출 안 됨
combine(emptyFlow(), flowOf(1, 2, 3)) { ... }

// ✅ onStart로 초기값 제공
combine(emptyFlow().onStart { emit(0) }, flowOf(1, 2, 3)) { ... }
```

### Zip
```kotlin
// ❌ 느린 Flow가 전체 속도를 지배
val slow = flowOf(1).onEach { delay(1000) }
val fast = flowOf(2, 3, 4)
slow.zip(fast) { ... } // 전체가 느려짐
```

### Merge
```kotlin
// ❌ 다른 타입 합치면 Any가 됨
val numbers = flowOf(1, 2, 3)
val strings = flowOf("a", "b", "c")
merge(numbers, strings) // Flow<Any>
```

## 참고 자료

- [Kotlin Coroutines - combine](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/combine.html)
- [Kotlin Coroutines - zip](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/zip.html)
- [Kotlin Coroutines - merge](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/merge.html)
