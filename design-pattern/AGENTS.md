<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-02-19 | Updated: 2026-02-19 -->

# design-pattern

## Purpose
GoF 디자인 패턴을 Kotlin으로 학습하고 구현하는 모듈. Bridge, Builder, Chain of Responsibility, Decorator, Delegate, Facade, Factory, Flyweight, Observer(MVC, push/pull), Proxy, Singleton, Strategy 패턴을 실험적으로 구현한다.

## Key Files

| File | Description |
|------|-------------|
| `build.gradle.kts` | 모듈 빌드 설정 (murjune-jvm-library) |

## Subdirectories

| Directory | Purpose |
|-----------|---------|
| `src/main/kotlin/com/murjune/practice/` | 디자인 패턴 구현 소스 |
| `src/test/kotlin/com/murjune/practice/` | 패턴 동작 검증 테스트 |

## Source Layout — main

| Package | Description |
|---------|-------------|
| `bridge/` | Bridge 패턴 |
| `builder/` | Builder 패턴 (주의: `design-pattern/src/main` 내 존재) |
| `chain_of_responsibility/` | Chain of Responsibility 패턴 |
| `decorate/` | Decorator 패턴 |
| `delegate/` | Delegate 패턴 |
| `facade/` | Facade 패턴 |
| `factory/` | Factory / Abstract Factory 패턴 |
| `fly_weight/` | Flyweight 패턴 |
| `observer/` | Observer 패턴 (MVC, non-observer, push, pull 변형 포함) |
| `proxy/` | Proxy 패턴 (RMI 연습, usecase1/2 포함) |
| `singletone/` | Singleton 패턴 |
| `strategy/` | Strategy 패턴 |

## For AI Agents

### Working In This Directory
- 각 패턴은 독립된 패키지에 구현한다.
- Observer 패턴은 여러 변형(MVC, push, pull, non-observer)을 비교하므로 구조를 유지한다.
- Proxy 패턴에 RMI 연습 코드 포함 — 실제 네트워크 연결 없이 인터페이스만 학습 목적.

### Testing Requirements
```bash
./gradlew :design-pattern:test
```

### Common Patterns
- 인터페이스 + 구현체 구조로 패턴 표현
- Kotlin의 `by` 키워드를 활용한 Delegate 패턴

## Dependencies

### External
- JUnit5, Kotest

<!-- MANUAL: Any manually added notes below this line are preserved on regeneration -->
