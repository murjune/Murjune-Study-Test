# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

이 프로젝트는 학습테스트를 통해 Kotlin, 코루틴, 알고리즘, 디자인 패턴 등을 공부하고 정리한 레포지토리입니다.
멀티 모듈 구조이며, JUnit5와 Kotest를 테스트 프레임워크로 사용합니다.

**학습 목적 레포지토리**: 실험과 학습이 우선. 프로덕션 코드 품질보다 이해와 기록이 중요.

## Project Structure

- **algorithm-practice**: 알고리즘과 자료구조 학습 및 구현
- **android-libs-practice**: Android Framework API 학습 (Uri 등)
- **coroutine**: 코루틴, 플로우 학습 및 테스트
- **design-pattern**: 디자인 패턴 학습 및 구현
- **kotlin-practice**: Kotlin 언어 기능 학습
- **java-practice**: Java 언어 기능 학습
- **compose-practice**: Jetpack Compose 기본 학습 (Navigation, Stability, LazyColumn 등)
- **compose-ui-practice**: Compose UI 고급 학습 (Animation, Custom Layout 등)
- **rxjava2**: RxJava2 학습 및 실습
- **build-logic**: 빌드 로직과 커스텀 플러그인들

## Study Workflow

학습은 `/study` skill을 사용하는 4단계 사이클로 진행됩니다.

```
/study              # 백로그 확인 후 자동 선택
/study "주제명"      # 특정 주제 바로 시작
```

**4단계 사이클:**
1. **TEACH** — 공식 문서 기반 개념 설명 + 핑퐁 Q&A → `study: teach <topic>` 커밋
2. **SAMPLE** — NowInAndroid/JetNews 베스트 프랙티스 → `study: sample <topic>` 커밋
3. **CHALLENGE** — 코딩 문제 구현 → `study: challenge <topic>` 커밋
4. **REVIEW** — 내 코드 vs 정답 코드 리뷰 → `study: complete <topic>` 커밋

**학습 상태 추적:** `STUDY_BACKLOG.md` (프로젝트 루트)
- 각 Phase 완료 시 상태 자동 업데이트
- 전 모듈 학습 진행 현황 통합 관리

**각 토픽별 학습 내용:** `<module>/study/plan/<topic>/README.md`

## Build System

- **Gradle with Kotlin DSL** 사용
- **Version Catalog** (`gradle/libs.versions.toml`)로 의존성 관리
- **커스텀 Convention Plugins** (`build-logic/`) 사용
- **ktlint** 자동 적용 (모든 프로젝트)

### 주요 커스텀 플러그인
- `murjune-jvm-library`: JVM 라이브러리 설정
- `murjune-unit-test`: 단위 테스트 설정 (JUnit5, Kotest)
- `murjune-android-application`: Android 앱 설정
- `murjune-android-library`: Android 라이브러리 설정
- `murjune-android-feature`: Navigation + Serialization 포함 Feature 모듈

## Common Commands

### 빌드 및 테스트
```bash
# 전체 테스트
./gradlew test

# 모듈별 테스트
./gradlew :coroutine:test
./gradlew :kotlin-practice:test
./gradlew :algorithm-practice:test
./gradlew :android-libs-practice:test
./gradlew :compose-practice:test          # Robolectric 포함

# Lint
./gradlew ktlintCheck
./gradlew ktlintFormat
```

### Android 모듈
```bash
./gradlew :compose-practice:assembleDebug
./gradlew :compose-ui-practice:assembleDebug
./gradlew :compose-practice:testDebugUnitTest
./gradlew :compose-ui-practice:connectedAndroidTest
```

## Test Frameworks

| 모듈 유형 | 프레임워크 |
|-----------|-----------|
| JVM 모듈 | JUnit5 + Kotest + MockK |
| Android 모듈 (단위) | Robolectric + Compose UI Test |
| Android 모듈 (통합) | Instrumented Test (AndroidJUnit5) |

### 테스트 작성 시 참고사항
- JVM 모듈: `src/test/` — JUnit5 + Kotest 사용
- Android 단위 테스트: `src/test/` — Robolectric (`isIncludeAndroidResources = true` 설정됨)
- Android 통합 테스트: `src/androidTest/` — Espresso + Compose UI Test
- 코루틴 테스트: `kotlinx-coroutines-test` 활용
- Navigation 테스트: `androidx.navigation:navigation-testing` + Robolectric

## Dependencies (주요 버전)

의존성은 `gradle/libs.versions.toml`에서 중앙 관리:

- Kotlin: 2.2.21
- kotlinx-coroutines: 1.10.2
- Compose BOM: 2025.11.00
- Navigation Compose: 2.9.6
- JUnit5: 6.0.1
- Kotest: 6.0.4
- MockK: 1.14.6
- Robolectric: 4.16

## Commit Convention

학습 커밋 메시지 패턴:
```
study: teach <topic>      # Phase 1 완료
study: sample <topic>     # Phase 2 완료
study: challenge <topic>  # Phase 3 완료
study: complete <topic>   # Phase 4 완료 (전체 사이클)

docs: update README for <topic>
docs: update STUDY_BACKLOG
```

일반 커밋:
```
feat: <기능 추가>
fix: <버그 수정>
refactor: <리팩토링>
chore: <설정 변경>
```

## Development Notes

- 모든 모듈에 ktlint 자동 적용 → 커밋 전 `./gradlew ktlintFormat` 권장
- 학습 결과는 각 토픽 디렉토리의 README.md에 기록
- 학습 진행 현황은 `STUDY_BACKLOG.md`에서 통합 관리
- trailing comma 사용, 함수에 명시적 반환 타입 선언
