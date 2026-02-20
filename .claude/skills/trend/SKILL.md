---
name: trend
description: "최신 Android/Jetpack API 트렌드를 조사하여 STUDY_BACKLOG.md에 새 학습 항목 추가. KMP/CMP 제외."
allowed-tools:
  - WebSearch
  - WebFetch
  - Read
  - Edit
  - Glob
  - Grep
  - Bash
---

# Trend Skill — 최신 Android API 트렌드 스카우터

최신 Android/Jetpack/Compose API 변경사항을 조사하고 STUDY_BACKLOG.md에 새 학습 항목을 추가합니다.

## 사용법

```
/trend                    # 최신 트렌드 조사 + 백로그 업데이트
/trend "Compose 1.11"     # 특정 주제 범위 지정
```

---

## 실행 모드

### 수동 실행 (`/trend`)
사용자가 직접 호출. 현재 세션에서 포그라운드로 실행하고 결과를 대화에 출력한다.

### 자동 실행 (SessionStart 훅 트리거)
`[TREND_UPDATE_NEEDED]` 훅 메시지를 감지하면:
1. 사용자에게 **"백그라운드에서 최신 Android API 트렌드를 업데이트 중입니다"** 안내
2. `Task(subagent_type='general-purpose', run_in_background=true)`로 아래 실행 단계(1~6)를 **커밋 포함하여** 전부 위임
3. 사용자의 원래 요청을 즉시 처리 (블로킹하지 않음)
4. 백그라운드 에이전트가 완료되면 결과를 요약해서 사용자에게 알림

> **중요:** 백그라운드 에이전트는 반드시 6단계 커밋까지 완료해야 한다. 커밋이 되어야 다음 세션에서 훅이 staleness를 올바르게 판단할 수 있다.

---

## 실행 단계

### 1. 현재 백로그 분석

1. `STUDY_BACKLOG.md` 읽기
2. 이미 등록된 주제 목록 추출 (중복 방지용)
3. 마지막 트렌드 업데이트 날짜 확인

---

### 2. 최신 API 조사 (WebSearch)

다음 카테고리별로 **최소 4개 이상** WebSearch 수행:

**필수 검색 쿼리:**
```
"Jetpack Compose latest release {current_year} new APIs stable"
"Android {latest_version} new platform APIs developer features"
"AndroidX library releases {current_year} changelog new"
"Jetpack ViewModel Lifecycle SavedStateHandle new APIs {current_year}"
"Android architecture components new {current_year}"
```

**선택 검색 (변경사항이 있을 법한 영역):**
```
"Jetpack Navigation new version {current_year}"
"Android Gradle Plugin new features {current_year}"
"Kotlin Android extensions new {current_year}"
"Jetpack Room Datastore WorkManager new APIs {current_year}"
```

**제외 대상 (검색 결과에서 필터링):**
- KMP (Kotlin Multiplatform)
- CMP (Compose Multiplatform)
- Flutter, React Native 등 크로스플랫폼
- 이미 deprecated된 API

---

### 3. 신규 항목 선별

검색 결과에서 다음 기준으로 필터링:

**포함 기준:**
- stable 또는 beta 이상으로 릴리즈된 신규 API
- 기존 API의 중요한 변경사항 (breaking change, 신규 함수 등)
- 개발자가 실무에서 알아야 할 변경
- 성능 개선 관련 새로운 메커니즘

**제외 기준:**
- 이미 STUDY_BACKLOG.md에 등록된 주제
- alpha 단계의 실험적 API (단, 주목도가 매우 높은 경우 예외)
- 단순 버그 수정, 마이너 패치

---

### 4. 백로그 업데이트

새 항목을 적절한 모듈 섹션의 **기존 항목 아래에** 추가.

**항목 포맷:**
```markdown
| <주제명> | <우선순위> | 🆕 <YYYY-MM-DD>. <한줄 설명> |
```

**우선순위 기준:**
- **높음**: stable 릴리즈된 신규 핵심 API, breaking change, 필수 대응
- **중**: beta/RC 단계이나 주목할 만한 변경, 성능 관련
- **낮음**: 알아두면 좋은 부가 API, 도구 변경

**모듈 매핑:**
| 주제 영역 | 대상 모듈 |
|-----------|----------|
| Compose Runtime/State/Effect | compose-practice |
| Compose UI/Animation/Layout | compose-ui-practice |
| Lifecycle/ViewModel/SavedState | android-architecture |
| Android 플랫폼 API (알림, 권한 등) | android-libs-practice |
| 코루틴/Flow | coroutine |
| Kotlin 언어 기능 | kotlin-practice |

> 대상 모듈 섹션이 백로그에 없으면 새로 만든다.

---

### 5. 결과 보고

추가된 항목 요약을 사용자에게 출력:

```
## 🆕 트렌드 업데이트 결과 (YYYY-MM-DD)

| 주제 | 모듈 | 우선순위 | 추가 이유 |
|------|------|----------|----------|
| ... | ... | ... | ... |

Sources:
- [출처1](URL)
- [출처2](URL)
```

추가할 새 항목이 없는 경우:
```
현재 백로그가 최신 상태입니다. 새로 추가할 항목이 없습니다.
마지막 확인: YYYY-MM-DD
```

---

### 6. 커밋

```
docs: update STUDY_BACKLOG with latest API trends

- <추가된 항목 수>개 신규 학습 항목 추가
- 조사일: YYYY-MM-DD
- 주요 항목: <상위 2-3개 나열>
```

---

## 규칙

- **WebSearch 결과 기반만** — 추측이나 기억에 의존하지 않음
- **중복 금지** — 이미 백로그에 있는 주제는 절대 추가하지 않음
- **출처 필수** — 결과 보고 시 반드시 참고 URL 포함
- **날짜 기록** — 각 항목 메모에 추가 날짜 표기
- **KMP/CMP 제외** — 크로스플랫폼 관련 항목 필터링
- **최대 10개** — 한 번 실행에 최대 10개 항목까지만 (과도한 항목 방지)
- **한국어** — 모든 출력과 백로그 항목은 한국어로 작성
