<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-02-19 | Updated: 2026-02-19 -->

# build-logic

## Purpose
멀티모듈 프로젝트의 공통 빌드 설정을 캡슐화한 커스텀 Gradle Convention Plugin 모듈. 각 앱/라이브러리 모듈이 이 플러그인들을 적용해 빌드 설정을 일관되게 유지한다.

## Key Files

| File | Description |
|------|-------------|
| `settings.gradle.kts` | build-logic 자체의 모듈 설정 |
| `gradle.properties` | build-logic Gradle 속성 |
| `convention/build.gradle.kts` | convention 모듈 빌드 설정 및 플러그인 등록 |

## Subdirectories

| Directory | Purpose |
|-----------|---------|
| `convention/` | 커스텀 Convention Plugin 구현체 (see `convention/AGENTS.md`) |

## For AI Agents

### Working In This Directory
- 새 Convention Plugin 추가 시: `convention/src/main/kotlin/`에 플러그인 파일 생성 후 `convention/build.gradle.kts`의 `gradlePlugin { plugins { ... } }` 블록에 등록.
- `libs.versions.toml`의 `[plugins]` 섹션에도 `murjune-*` 항목 추가.
- 플러그인 변경 후 전체 빌드로 검증: `./gradlew build`.

### Common Patterns
- `Project.extensions.configure<>()` 로 Android/Kotlin 확장 설정
- `dependencies { }` 블록에서 `libs.*` Version Catalog 참조

## Dependencies

### External
- `android-gradlePlugin` — AGP
- `kotlin-gradlePlugin` — Kotlin Gradle Plugin
- `compose-gradlePlugin` — Compose Compiler Plugin
- `ksp-gradlePlugin` — KSP

<!-- MANUAL: Any manually added notes below this line are preserved on regeneration -->
