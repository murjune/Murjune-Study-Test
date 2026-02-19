<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-02-19 | Updated: 2026-02-19 -->

# coroutine

## Purpose
Kotlin 코루틴과 Flow를 깊이 이해하기 위한 학습테스트 모듈. 코루틴 기초부터 고급 개념(취소, 예외처리, 스코프, 디스패처, 채널, Flow 연산자, SharedFlow, StateFlow)까지 실험적 코드와 단위 테스트로 정리한다.

## Key Files

| File | Description |
|------|-------------|
| `build.gradle.kts` | 모듈 빌드 설정 (murjune-jvm-library, murjune-unit-test) |
| `docs/` | 코루틴/예외/테스트 관련 마크다운 문서 모음 |

## Subdirectories

| Directory | Purpose |
|-----------|---------|
| `src/main/kotlin/com/murjune/practice/` | 실험용 메인 소스 코드 (see below) |
| `src/test/kotlin/com/murjune/practice/` | 학습 테스트 코드 (see below) |
| `docs/` | 코루틴 개념 정리 문서 |

## Source Layout — main

| Package | Description |
|---------|-------------|
| `callbackflow/` | CallbackFlow 예제 (카메라 콜백 래핑) |
| `channel/` | Channel 기본 (Actor 패턴 포함) |
| `channelflow/` | ChannelFlow 예제 |
| `coroutinescope/` | 커스텀 CoroutineScope 구현 |
| `dispatcher/` | 커스텀 Dispatcher 및 예제 |
| `exception/` | 예외 처리 예제 (async 예외, CancellationException, ExceptionHandler, SupervisorJob) |
| `job/` | Job 계층 구조 예제 |
| `suspendcoroutine/` | suspendCoroutine / suspendCancellableCoroutine 예제 |
| `suspense/` | suspend 함수 기초 예제 |
| `utils/` | EventFlow, LaunchUtils, Logger 등 유틸리티 |

## Source Layout — test

| Package | Description |
|---------|-------------|
| `cancel/` | 코루틴 취소 주의사항 테스트 |
| `cas/` | CAS(Compare-And-Swap) 기반 SpinLock 학습 |
| `channel/` | Channel 기본/Producer/StateHolder/TrySend 테스트 |
| `continuation/` | CancellableContinuation 학습 테스트 |
| `coroutine_test/` | 기본 코루틴 테스트, BackgroundScope, ViewModel 연동 |
| `coroutinescope/` | 커스텀 Scope 동작 테스트 |
| `exception/supervisor/` | SupervisorJob 예외 전파 테스트 |
| `flow/eventflow/` | EventFlow(SharedFlow 기반) 테스트 |
| `flow/exception/` | Flow 예외 처리 테스트 |
| `flow/operator/` | combine, merge, zip, debounce, flatXXX, onStart 연산자 테스트 |
| `flow/sharedflow/` | SharedFlow vs Channel 비교, SharedFlow 연산자 테스트 |
| `flow/stateflow/` | MutableStateFlow 성능/update 테스트 |
| `suspense/` | 기본 suspend 함수 테스트 |
| `utils/` | CoroutineScopeUtils, JobTestUtils |

## For AI Agents

### Working In This Directory
- 학습테스트 목적이므로 테스트 클래스 하나에 하나의 개념만 집중하는 패턴 유지.
- `kotlinx-coroutines-test`의 `runTest`, `TestScope`, `UnconfinedTestDispatcher`를 활용한다.
- Flow 테스트 시 `turbine` 라이브러리 대신 직접 `collect`를 사용하거나 `toList()`를 활용한다.
- 새 학습 주제는 적절한 패키지 아래에 새 파일을 만든다.

### Testing Requirements
```bash
./gradlew :coroutine:test
```
- JUnit5 + Kotest 모두 사용 가능.
- 코루틴 테스트는 `runTest` 블록 안에서 작성.

### Common Patterns
- `CoroutinesTestExtension` 또는 `@OptIn(ExperimentalCoroutinesApi::class)` 사용
- `TestCoroutineScheduler` / `advanceUntilIdle()` / `advanceTimeBy()` 활용

## Dependencies

### Internal
- `utils/EventFlow.kt` — SharedFlow 기반 단발성 이벤트 유틸리티

### External
- `kotlinx-coroutines-core` — 코루틴 핵심
- `kotlinx-coroutines-test` — 코루틴 테스트 지원
- JUnit5, Kotest, MockK

<!-- MANUAL: Any manually added notes below this line are preserved on regeneration -->