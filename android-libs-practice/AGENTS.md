<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-02-19 | Updated: 2026-02-19 -->

# android-libs-practice

## Purpose
Android 플랫폼 라이브러리를 학습하는 Android 모듈. URI 처리 등 Android 고유 API를 실험하고 테스트한다.

## Key Files

| File | Description |
|------|-------------|
| `build.gradle.kts` | Android 라이브러리 빌드 설정 |

## Subdirectories

| Directory | Purpose |
|-----------|---------|
| `src/main/kotlin/com/murjune/practice/android/libs/` | Android 라이브러리 학습 소스 |
| `src/test/kotlin/com/murjune/practice/android/libs/uri/` | URI 처리 테스트 |
| `src/test/kotlin/com/murjune/practice/android/libs/uri/docs/` | URI 관련 학습 문서 |
| `src/main/res/` | Android 리소스 |

## For AI Agents

### Working In This Directory
- Android 라이브러리 모듈이므로 Android SDK 의존성이 있다.
- URI 테스트는 Robolectric을 통해 JVM 환경에서 실행 가능할 수 있음.

### Testing Requirements
```bash
./gradlew :android-libs-practice:test
```

## Dependencies

### External
- Android SDK (core-ktx 등)
- JUnit5 Android, Robolectric

<!-- MANUAL: Any manually added notes below this line are preserved on regeneration -->
