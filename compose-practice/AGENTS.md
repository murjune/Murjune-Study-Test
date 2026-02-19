<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-02-19 | Updated: 2026-02-19 -->

# compose-practice

## Purpose
Jetpack Compose 기본 개념을 학습하는 Android 모듈. LazyColumn/LazyRow, Composable 안정성(Stability), Navigation, ViewModel 연동 등의 Compose 핵심 주제를 샘플 앱과 UI 테스트로 탐구한다.

## Key Files

| File | Description |
|------|-------------|
| `build.gradle.kts` | Android 앱 빌드 설정 (murjune-android-application, murjune-android-compose) |

## Subdirectories

| Directory | Purpose |
|-----------|---------|
| `src/main/java/com/murjune/pratice/compose/study/` | Compose 학습용 샘플 코드 |
| `src/androidTest/java/com/murjune/pratice/compose/study/` | Compose UI 테스트 |
| `src/main/res/` | Android 리소스 (drawable, mipmap, values) |
| `study/` | 학습 계획 및 진행 상황 문서 |

## Source Layout — main

| Package | Description |
|---------|-------------|
| `study/sample/navigation/` | Navigation Compose 샘플 |
| `study/sample/viewmodel/` | ViewModel + Compose 연동 샘플 |

## Source Layout — androidTest

| Package | Description |
|---------|-------------|
| `study/lazycomposable/` | LazyColumn/Row UI 테스트 |
| `study/stability/` | Composable 안정성(Stability) 관련 UI 테스트 |

## Study Documents

| Directory | Purpose |
|-----------|---------|
| `study/done/` | 완료된 학습 주제 정리 |
| `study/plan/` | 학습 계획 (lazy-column, stability, study-lazy-composable) |
| `study/progress/` | 진행 중인 학습 주제 |

## For AI Agents

### Working In This Directory
- Android 모듈이므로 빌드/테스트에 Android SDK 환경 필요.
- UI 테스트는 `androidTest`에, 단위 테스트는 `test`에 작성.
- Composable 안정성 이슈 실험 시 `@Stable`, `@Immutable`, `@Stable` 어노테이션 활용.

### Testing Requirements
```bash
# 빌드
./gradlew :compose-practice:assembleDebug

# UI 테스트 (에뮬레이터/기기 필요)
./gradlew :compose-practice:connectedAndroidTest
```

### Common Patterns
- `@Composable` 함수는 상태 호이스팅(State Hoisting) 패턴 준수
- `remember` / `rememberSaveable` 로 상태 관리
- Preview는 `@Preview` 어노테이션 사용

## Dependencies

### External
- Compose BOM 2025.11.00
- `androidx-navigation-compose`
- `androidx-lifecycle-viewModelCompose`
- JUnit5 Android, Espresso

<!-- MANUAL: Any manually added notes below this line are preserved on regeneration -->
