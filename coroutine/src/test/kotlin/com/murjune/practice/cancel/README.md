# Coroutine Cancellation (코루틴 취소) 학습

코루틴의 취소(Cancellation)는 **협력적(Cooperative)** 이다. 즉, 코루틴이 스스로 취소 신호를 확인하고 중단해야 한다.

## 핵심 개념

### 1. 협력적 취소 (Cooperative Cancellation)
- `cancel()` 호출 시 즉시 취소되는 것이 아님
- **중단 지점(suspension point)** 에서 `CancellationException` 발생
- 중단 지점: `delay()`, `yield()`, suspend 함수 호출 등

### 2. 중단 지점이 없으면 취소 안됨
```kotlin
// ❌ 취소 안됨
while (true) {
    // 중단 지점이 없음
}

// ✅ 취소됨
while (true) {
    delay(100) // 중단 지점
}
```

## 주요 주의사항

### ⚠️ Case 1: try-catch로 CancellationException 잡아먹지 않기

**문제:**
```kotlin
repeat(10) { i ->
    try {
        fetchData(i)
    } catch (e: Exception) {
        // ❌ CancellationException을 잡아먹어서 다음 루프가 계속 실행됨
        println("Error: ${e.message}")
    }
}
```

**해결:**
```kotlin
repeat(10) { i ->
    try {
        fetchData(i)
    } catch (e: Exception) {
        coroutineContext.ensureActive() // ✅ 취소 상태 확인
        println("Other exception: ${e.message}")
    }
}
```

**핵심:**
- `catch` 블록에서 `coroutineContext.ensureActive()` 호출 필수
- 취소된 상태라면 `CancellationException`을 다시 던짐

### ⚠️ Case 2: finally에서 suspend 함수 호출 시 withContext(NonCancellable) 사용

**문제:**
```kotlin
try {
    // 작업 수행
} finally {
    cleanUpResources() // ❌ 취소된 상태에서 suspend 함수 호출 -> 실행 안됨
}
```

**해결:**
```kotlin
try {
    // 작업 수행
} finally {
    withContext(NonCancellable) {
        cleanUpResources() // ✅ NonCancellable 컨텍스트에서 안전하게 실행
    }
}
```

**핵심:**
- `NonCancellable`은 취소가 불가능한 컨텍스트 제공
- 리소스 정리 작업을 안전하게 수행

### ⚠️ Case 3: CoroutineScope 취소 vs 자식 코루틴만 취소

#### 전체 스코프 취소 (재사용 불가)
```kotlin
val scope = CoroutineScope(Dispatchers.IO)
scope.cancel() // ❌ 전체 스코프 취소 - 재사용 불가

scope.launch {
    // ❌ 실행 안됨
}
```

#### 자식만 취소 (재사용 가능)
```kotlin
val scope = CoroutineScope(Dispatchers.IO)
scope.coroutineContext.cancelChildren() // ✅ 자식만 취소

scope.launch {
    // ✅ 실행됨
}
```

**핵심:**
- `scope.cancel()`: 전체 스코프 취소, 재사용 불가
- `scope.coroutineContext.cancelChildren()`: 자식만 취소, 스코프는 재사용 가능

### ⚠️ Case 4: 긴 연산/IO 작업에서 ensureActive() 체크

**문제:**
```kotlin
for (i in 0..10000) {
    Thread.sleep(10) // ❌ blocking 작업 - 중단 지점 없음
    processData(i)
}
```

**해결:**
```kotlin
for (i in 0..10000) {
    coroutineContext.ensureActive() // ✅ 주기적으로 취소 상태 확인
    Thread.sleep(10)
    processData(i)
}
```

**핵심:**
- blocking 작업은 자동으로 취소되지 않음
- 긴 작업 도중 주기적으로 `ensureActive()` 호출

## 취소 관련 함수들

| 함수 | 설명 | 사용 시점 |
|------|------|---------|
| `job.cancel()` | Job 취소 요청 | 즉시 반환, 취소 완료까지 기다리지 않음 |
| `job.join()` | Job 완료까지 대기 | cancel() 후 사용하면 취소 완료까지 대기 |
| `job.cancelAndJoin()` | 취소 요청 + 완료 대기 | cancel() + join()을 하나로 |
| `scope.cancel()` | CoroutineScope 전체 취소 | 스코프 재사용 불가 |
| `scope.coroutineContext.cancelChildren()` | 자식 코루틴만 취소 | 스코프 재사용 가능 |
| `coroutineContext.ensureActive()` | 취소 상태 확인 | catch 블록, 긴 작업 중 |

## 실전 활용 패턴

### 패턴 1: 네트워크 요청 중 취소
```kotlin
suspend fun fetchUserData(): User {
    return withContext(Dispatchers.IO) {
        try {
            api.getUser()
        } catch (e: Exception) {
            coroutineContext.ensureActive() // 취소 확인
            throw NetworkException(e)
        }
    }
}
```

### 패턴 2: 대용량 파일 처리 중 취소
```kotlin
suspend fun processLargeFile(file: File) {
    withContext(Dispatchers.IO) {
        file.forEachLine { line ->
            coroutineContext.ensureActive() // 주기적으로 취소 확인
            processLine(line)
        }
    }
}
```

### 패턴 3: ViewModel에서 화면 종료 시 취소
```kotlin
class MyViewModel : ViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.cancelChildren() // 자식만 취소
    }
}
```

## 학습 테스트 파일

- **`CoroutineCancelCautionTest.kt`**: 취소 시 주의사항 학습 테스트
  - Case 1: try-catch로 CancellationException 잡아먹는 문제
  - Case 2: finally에서 suspend 함수 호출 문제
  - Case 3: CoroutineScope 취소 vs 자식 코루틴만 취소
  - Case 4: 긴 연산/IO 작업에서 취소 체크

## 핵심 요약

1. **협력적 취소**: 코루틴은 중단 지점에서만 취소됨
2. **ensureActive()**: catch 블록과 긴 작업에서 필수
3. **NonCancellable**: finally에서 리소스 정리 시 사용
4. **cancelChildren()**: 스코프 재사용이 필요하면 자식만 취소
5. **blocking 작업**: 주기적으로 취소 상태 확인 필요

## 참고 자료

- [Kotlin Coroutines 공식 문서 - Cancellation and Timeouts](https://kotlinlang.org/docs/cancellation-and-timeouts.html)
- [코루틴 공식 가이드 - Cancellation is cooperative](https://kotlinlang.org/docs/cancellation-and-timeouts.html#cancellation-is-cooperative)
