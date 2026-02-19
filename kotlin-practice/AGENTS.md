<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-02-19 | Updated: 2026-02-19 -->

# kotlin-practice

## Purpose
Kotlin 언어 기능을 학습테스트 방식으로 탐구하는 모듈. 제네릭, 람다/클로저, 컬렉션, 위임, 리플렉션, 직렬화, Sequence, 정규표현식, 날짜/시간, MockK 사용법 등 Kotlin의 다양한 기능을 테스트 코드로 정리한다.

## Key Files

| File | Description |
|------|-------------|
| `build.gradle.kts` | 모듈 빌드 설정 |

## Subdirectories

| Directory | Purpose |
|-----------|---------|
| `src/main/kotlin/com/murjune/practice/` | 학습용 주요 소스 코드 |
| `src/test/kotlin/com/murjune/practice/` | 언어 기능별 학습 테스트 |

## Source Layout — main

| Package | Description |
|---------|-------------|
| `generic/` | 제네릭 타입 파라미터, 공변/반변 실험 |
| `lamda/` | 람다 기초, capturing, closure |
| `scope/` | 스코프 함수 (let, run, apply, also, with) |
| `sequence/` | Kotlin Sequence 지연 평가 학습 |

## Source Layout — test

| Package | Description |
|---------|-------------|
| `annotation/` | 어노테이션 처리 학습 |
| `class/` | 클래스 특성 (data class, sealed, object 등) |
| `closure/` | 클로저 캡처 동작 테스트 |
| `collection/` | 컬렉션 API 학습 |
| `conversion/` | 타입 변환 학습 |
| `delegate/` | 프로퍼티 위임 (by lazy, by Delegates) |
| `document/` | 문서화 관련 학습 |
| `localdate/` | kotlinx-datetime 학습 |
| `math/` | 수학 연산 유틸리티 학습 |
| `mockk/` | MockK 라이브러리 사용법 학습 |
| `reflection/` | 리플렉션 API 학습 |
| `regex/` | 정규표현식 학습 |
| `sequence/` | Sequence 연산자 학습 테스트 |
| `serialization/` | kotlinx-serialization / Gson 커스텀 직렬화 학습 |
| `stlib/` | Kotlin 표준 라이브러리 함수 학습 |
| `utils/` | 테스트 유틸리티 |

## For AI Agents

### Working In This Directory
- 각 테스트 파일은 하나의 Kotlin 언어 기능에 집중한다.
- `src/main`에는 테스트에서 사용할 보조 클래스/함수를 넣는다.
- 학습 목적이므로 명확한 주석이나 테스트 이름으로 의도를 설명한다.

### Testing Requirements
```bash
./gradlew :kotlin-practice:test
```

### Common Patterns
- JUnit5 `@Test`, Kotest `FunSpec` / `BehaviorSpec` 모두 사용
- `kotlinx-serialization` 사용 시 `@Serializable` 어노테이션 필수
- Gson 커스텀 직렬화는 `JsonDeserializer` / `JsonSerializer` 구현

## Dependencies

### External
- `kotlinx-serialization-json` — JSON 직렬화
- `kotlinx-datetime` — 날짜/시간 처리
- `gson` — Gson JSON 라이브러리
- `mockk` — 목킹 라이브러리
- JUnit5, Kotest

<!-- MANUAL: Any manually added notes below this line are preserved on regeneration -->