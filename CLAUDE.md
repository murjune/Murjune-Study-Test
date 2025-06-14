# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

이 프로젝트는 학습테스트를 통해 Kotlin, 코루틴, 알고리즘, 디자인 패턴 등을 공부하고 정리한 레포지토리입니다. 멀티 모듈 구조로 되어 있으며, JUnit5와 Kotest를 테스트 프레임워크로 사용합니다.

## Project Structure

- **algorithm-practice**: 알고리즘과 자료구조 학습 및 구현
- **coroutine**: 코루틴, 플로우 학습 및 테스트
- **design-pattern**: 디자인 패턴 학습 및 구현
- **kotlin-practice**: Kotlin 언어 기능 학습
- **java-practice**: Java 언어 기능 학습
- **compose-practice**: Jetpack Compose 기본 학습
- **compose-ui-practice**: Compose UI 고급 학습
- **rxjava2**: RxJava2 학습 및 실습
- **build-logic**: 빌드 로직과 커스텀 플러그인들

## Build System

- **Gradle with Kotlin DSL** 사용
- **Version Catalog** (`gradle/libs.versions.toml`)로 의존성 관리
- **커스텀 Convention Plugins** (`build-logic/`) 사용
- **ktlint** 자동 적용 (모든 프로젝트)

### 주요 커스텀 플러그인
- `murjune-jvm-library`: JVM 라이브러리 설정
- `murjune-unit-test`: 단위 테스트 설정 (JUnit5, Kotest)
- `murjune-android-*`: 안드로이드 관련 설정들

## Common Commands

### 빌드 및 테스트
```bash
# 전체 프로젝트 빌드
./gradlew build

# 특정 모듈 테스트
./gradlew :coroutine:test
./gradlew :kotlin-practice:test
./gradlew :algorithm-practice:test

# 전체 테스트 실행
./gradlew test

# Lint 실행 (ktlint)
./gradlew ktlintCheck

# Lint 자동 수정
./gradlew ktlintFormat
```

### Android 모듈 (compose-practice, compose-ui-practice)
```bash
# Android 앱 빌드
./gradlew :compose-practice:assembleDebug
./gradlew :compose-ui-practice:assembleDebug

# Android 테스트
./gradlew :compose-practice:testDebugUnitTest
./gradlew :compose-ui-practice:connectedAndroidTest
```

## Test Frameworks

- **JUnit5**: 주요 테스트 프레임워크
- **Kotest**: 코틀린 전용 테스트 프레임워크
- **MockK**: 모킹 라이브러리
- **Coroutines Test**: 코루틴 테스트 지원

### 테스트 작성 시 참고사항
- 대부분의 모듈에서 JUnit5와 Kotest 모두 사용 가능
- 안드로이드 모듈에서는 JUnit5 Android Test Core 사용
- 코루틴 테스트 시 `kotlinx-coroutines-test` 활용

## Dependencies

주요 의존성들은 `gradle/libs.versions.toml`에서 중앙 관리됩니다:
- Kotlin 2.1.0
- Coroutines 1.10.1
- Compose BOM 2024.12.01
- JUnit5 5.10.2
- Kotest 5.8.0

## Development Notes

- 모든 모듈에 ktlint가 자동 적용되어 코드 스타일이 통일됨
- 학습테스트 목적이므로 실제 프로덕션 코드보다는 학습과 실험에 중점
- 각 모듈별로 README.md와 상세 문서들이 있음 (특히 coroutine, algorithm-practice)