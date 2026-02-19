<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-02-19 | Updated: 2026-02-19 -->

# rxjava2

## Purpose
RxJava2의 핵심 개념(Observable, Observer, Flowable, Subject)을 학습하는 JVM 모듈. 반응형 프로그래밍 패러다임을 이해하고, 코루틴/Flow와의 차이점을 비교하는 실험 코드를 포함한다.

## Key Files

| File | Description |
|------|-------------|
| `build.gradle.kts` | 모듈 빌드 설정 (RxJava2 의존성) |

## Source Layout — test

| Package/File | Description |
|---------|-------------|
| `rxjava2/ObservableTest.kt` | Observable 기본 동작 테스트 |
| `rxjava2/FlowableTest.kt` | Flowable(배압 지원) 기본 테스트 |
| `rxjava2/FlowableTest2.kt` | Flowable 에러 핸들링 테스트 |
| `rxjava2/SubjectTest.kt` | Subject(PublishSubject 등) 테스트 |
| `rxjava2/basic/ObservableBasicTest.kt` | Observable 세부 동작 기초 테스트 |
| `rxjava2/basic/ObserverBasicTest.kt` | Observer 인터페이스 구현 학습 |

## For AI Agents

### Working In This Directory
- RxJava2와 Kotlin 코루틴은 별개의 반응형 프로그래밍 접근법임을 테스트에 명시.
- `TestObserver` / `TestScheduler`를 활용해 비동기 동작 테스트.
- 새 RxJava2 개념 학습 시 `basic/` 또는 최상위 패키지에 적절히 배치.

### Testing Requirements
```bash
./gradlew :rxjava2:test
```

### Common Patterns
- `Observable.create()` / `Observable.just()` / `Observable.fromIterable()` 사용
- `subscribeOn(Schedulers.io())` + `observeOn(Schedulers.computation())`
- `Flowable` + 배압 전략 (`BackpressureStrategy.BUFFER` 등)
- `Subject`를 이용한 hot observable 구현

## Dependencies

### External
- RxJava2 (`io.reactivex.rxjava2:rxjava`)
- JUnit5, Kotest

<!-- MANUAL: Any manually added notes below this line are preserved on regeneration -->
