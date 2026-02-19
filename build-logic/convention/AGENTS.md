<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-02-19 | Updated: 2026-02-19 -->

# build-logic/convention

## Purpose
프로젝트 전체에 적용되는 Gradle Convention Plugin 구현체. Android 앱/라이브러리, Compose, JVM 라이브러리, 직렬화, 단위 테스트 등의 빌드 설정을 재사용 가능한 플러그인으로 캡슐화한다.

## Key Files

| File | Description |
|------|-------------|
| `AndroidApplicationPlugin.kt` | `murjune-android-application` — Android 앱 공통 설정 |
| `AndroidLibraryPlugin.kt` | `murjune-android-library` — Android 라이브러리 공통 설정 |
| `AndroidFeaturePlugin.kt` | `murjune-android-feature` — Feature 모듈 공통 설정 |
| `ComposeLibraryPlugin.kt` | `murjune-android-compose` — Compose 컴파일러 설정 |
| `JvmLibraryPlugin.kt` | `murjune-jvm-library` — JVM 라이브러리 공통 설정 |
| `KotlinSerializationPlugin.kt` | `murjune-kotlinx-serialization` — kotlinx-serialization 설정 |
| `UnitTestPlugin.kt` | `murjune-unit-test` — JUnit5 + Kotest 단위 테스트 설정 |
| `com/murjune/practice/plugins/ComposeConfigs.kt` | Compose 컴파일러 옵션 헬퍼 |
| `com/murjune/practice/plugins/KotlinConfigs.kt` | Kotlin 컴파일러 옵션 헬퍼 |
| `com/murjune/practice/plugins/ProjectExt.kt` | Project 확장 함수 |

## For AI Agents

### Working In This Directory
- 플러그인 ID는 `libs.versions.toml`의 `[plugins]` 섹션에 `murjune-*`으로 등록됨.
- 새 플러그인 추가 시:
  1. 이 디렉토리에 `XxxPlugin.kt` 파일 생성 (`Plugin<Project>` 구현)
  2. `convention/build.gradle.kts`의 `gradlePlugin { plugins { ... } }` 블록에 등록
  3. `gradle/libs.versions.toml`의 `[plugins]` 섹션에 추가
- ktlint는 모든 모듈에 자동으로 적용된다 (`UnitTestPlugin` 또는 루트 설정 참조).

### Common Patterns
```kotlin
// 전형적인 Convention Plugin 구조
class XxxPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("some.plugin")
            }
            extensions.configure<SomeExtension> {
                // 설정
            }
            dependencies {
                add("implementation", libs.findLibrary("some-lib").get())
            }
        }
    }
}
```

## Dependencies

### External
- AGP (Android Gradle Plugin)
- Kotlin Gradle Plugin
- Compose Compiler Gradle Plugin
- KSP Gradle Plugin

<!-- MANUAL: Any manually added notes below this line are preserved on regeneration -->
