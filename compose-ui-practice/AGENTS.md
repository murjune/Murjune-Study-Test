<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-02-19 | Updated: 2026-02-19 -->

# compose-ui-practice

## Purpose
Jetpack Compose UI 고급 주제를 학습하는 Android 모듈. 커스텀 레이아웃, 애니메이션, Canvas, 제스처, 테마 등 Compose UI의 심화 기능을 실험적으로 구현한다.

## Key Files

| File | Description |
|------|-------------|
| `build.gradle.kts` | Android 앱 빌드 설정 (murjune-android-application, murjune-android-compose) |
| `src/main/java/com/murjune/pratice/compose/ui/practice/MainActivity.kt` | 앱 진입점 (현재 수정됨) |

## Subdirectories

| Directory | Purpose |
|-----------|---------|
| `src/main/java/com/murjune/pratice/compose/ui/practice/` | Compose UI 고급 학습 코드 |
| `src/androidTest/java/com/murjune/pratice/compose/ui/practice/` | UI 테스트 |
| `src/test/java/com/murjune/pratice/compose/ui/practice/` | 단위 테스트 |
| `src/main/res/` | Android 리소스 |

## For AI Agents

### Working In This Directory
- `MainActivity.kt`가 현재 수정 중(git status: M)이므로 변경 전 내용을 반드시 확인.
- Android 모듈이므로 실제 디바이스 또는 에뮬레이터에서 시각적 확인 필요.
- `compose-practice`(기초)와 구분: 이 모듈은 고급 UI 기법에 집중.

### Testing Requirements
```bash
# 빌드
./gradlew :compose-ui-practice:assembleDebug

# UI 테스트
./gradlew :compose-ui-practice:connectedAndroidTest

# 단위 테스트
./gradlew :compose-ui-practice:testDebugUnitTest
```

### Common Patterns
- `Canvas` API로 커스텀 드로잉
- `Modifier` 체이닝으로 레이아웃/제스처 처리
- `LaunchedEffect`, `SideEffect`, `DisposableEffect` 생명주기 연동

## Dependencies

### External
- Compose BOM 2025.11.00
- `androidx-compose-foundation` (커스텀 레이아웃)
- JUnit5 Android, Espresso

<!-- MANUAL: Any manually added notes below this line are preserved on regeneration -->
