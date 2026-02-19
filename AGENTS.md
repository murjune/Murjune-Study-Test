<!-- Generated: 2026-02-19 | Updated: 2026-02-19 -->

# Murjune-Study-Test

## Purpose
Kotlin/Android 학습 테스트 레포지토리. 코루틴, 알고리즘, 디자인 패턴, Jetpack Compose, RxJava2 등을 학습테스트(Learning Test) 방식으로 탐구하는 멀티 모듈 프로젝트. 실제 프로덕션 코드보다 학습과 실험에 중점을 둔다.

## Key Files

| File | Description |
|------|-------------|
| `settings.gradle.kts` | 멀티모듈 설정 및 모듈 포함 목록 |
| `build.gradle.kts` | 루트 빌드 설정 |
| `gradle/libs.versions.toml` | Version Catalog — 모든 의존성 버전 중앙 관리 |
| `gradlew` / `gradlew.bat` | Gradle wrapper 실행 스크립트 |
| `CLAUDE.md` | Claude Code AI 에이전트용 프로젝트 가이드 |
| `README.md` | 프로젝트 설명 문서 |
| `gradle.properties` | Gradle 전역 속성 (JVM 힙 등) |

## Subdirectories

| Directory | Purpose |
|-----------|---------|
| `algorithm-practice/` | 알고리즘·자료구조 학습 및 구현 (see `algorithm-practice/AGENTS.md`) |
| `android-libs-practice/` | Android 라이브러리 학습 (URI 등) (see `android-libs-practice/AGENTS.md`) |
| `build-logic/` | 커스텀 Gradle Convention Plugins (see `build-logic/AGENTS.md`) |
| `compose-practice/` | Jetpack Compose 기본 학습 (see `compose-practice/AGENTS.md`) |
| `compose-ui-practice/` | Compose UI 고급 학습 (see `compose-ui-practice/AGENTS.md`) |
| `coroutine/` | 코루틴·Flow 학습 및 테스트 (see `coroutine/AGENTS.md`) |
| `design-pattern/` | 디자인 패턴 학습 및 구현 (see `design-pattern/AGENTS.md`) |
| `gradle/` | Gradle wrapper 및 Version Catalog |
| `java-practice/` | Java 언어 기능 학습 (see `java-practice/AGENTS.md`) |
| `kotlin-practice/` | Kotlin 언어 기능 학습 (see `kotlin-practice/AGENTS.md`) |
| `rxjava2/` | RxJava2 학습 및 실습 (see `rxjava2/AGENTS.md`) |

## For AI Agents

### Working In This Directory
- 모든 의존성 버전은 `gradle/libs.versions.toml`에서 관리한다. 직접 버전 문자열을 build.gradle.kts에 쓰지 말 것.
- 새로운 모듈 추가 시 `settings.gradle.kts`에 `include(":module-name")`을 추가해야 한다.
- 빌드 설정 변경 시 `build-logic/convention/` 플러그인을 우선 확인한다.
- 코드 스타일은 ktlint로 자동 강제된다. 커밋 전 `./gradlew ktlintFormat` 실행 권장.

### Testing Requirements
```bash
# 전체 테스트
./gradlew test

# 특정 모듈 테스트
./gradlew :coroutine:test
./gradlew :kotlin-practice:test
./gradlew :algorithm-practice:test

# lint 검사
./gradlew ktlintCheck

# lint 자동 수정
./gradlew ktlintFormat
```

### Common Patterns
- 모든 JVM 모듈: `murjune-jvm-library` 플러그인 사용
- 단위 테스트: `murjune-unit-test` 플러그인 (JUnit5 + Kotest)
- Android 모듈: `murjune-android-application` / `murjune-android-library` 플러그인
- Kotlin trailing comma 사용, 함수에 명시적 반환 타입 선언

## Dependencies

### External
- Kotlin 2.2.21
- kotlinx-coroutines 1.10.2
- Compose BOM 2025.11.00
- JUnit5 Jupiter 6.0.1
- Kotest 6.0.4
- MockK 1.14.6
- Gson 2.13.2
- RxJava2 (rxjava2 모듈 내 별도 설정)
- ktlint 14.0.1

<!-- MANUAL: Any manually added notes below this line are preserved on regeneration -->
