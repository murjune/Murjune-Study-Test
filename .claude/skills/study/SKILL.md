---
name: study
description: "Android/Kotlin 주제를 공식 문서 → 베스트 프랙티스 → 코딩 챌린지 → 코드 리뷰 4단계 사이클로 학습. 각 Phase 완료 시 STUDY_BACKLOG.md 상태 자동 업데이트 및 커밋."
allowed-tools:
  - WebFetch
  - WebSearch
  - Read
  - Write
  - Edit
  - Bash
  - Glob
  - Grep
  - Task
  - AskUserQuestion
---

# Study Skill — 인터랙티브 학습 컴패니언

Android/Kotlin 주제를 공식 문서 → 베스트 프랙티스 → 코딩 챌린지 → 코드 리뷰 사이클로 학습합니다.
각 Phase 완료 시 자동으로 커밋하고 STUDY_BACKLOG.md 상태를 업데이트합니다.

## 사용법

```
/study                          # 백로그에서 다음 토픽 자동 선택
/study "Navigation popUpTo"     # 특정 토픽 지정
/study --next                   # 백로그 다음 항목으로 넘어가기
/study --status                 # 현재 학습 진행 상태 확인
```

---

## 실행 단계

### 시작: 백로그 확인 및 토픽 선택

1. `STUDY_BACKLOG.md` 읽기
2. 토픽이 지정되지 않은 경우 → `AskUserQuestion`으로 백로그 항목 중 선택
3. STUDY_BACKLOG.md 업데이트: 선택 항목을 "진행 중 🔄 (Phase 1)" 상태로 변경
4. 해당 모듈의 `study/plan/<topic>/README.md` 확인 (없으면 생성)

---

### Phase 1: TEACH (공식 문서 + 우수 샘플 기반)

**목표:** 핵심 개념을 공식 문서와 실제 우수 샘플 코드 기반으로 정확하게 이해시킨다.

> **[STRICT REQUIREMENT]**
> Phase 1은 반드시 아래 두 가지를 모두 수행한 후에만 설명을 시작한다.
> 토큰 비용이나 시간이 오래 걸려도 절대 생략하지 않는다.
>
> 1. **공식 문서 fetch 필수** — `WebFetch`로 해당 토픽의 공식 문서를 실제로 가져와야 함.
>    기억에만 의존하거나 추측으로 설명하는 것은 금지.
>    - 우선순위: developer.android.com > kotlinlang.org > Android 공식 GitHub
>
> 2. **우수 샘플 코드 fetch 필수** — NowInAndroid, JetNews, Android Snippets 중
>    해당 토픽과 가장 관련 있는 코드를 실제로 읽어온 후 설명에 반영해야 함.
>    - NowInAndroid: `https://github.com/android/nowinandroid`
>    - Compose Samples: `https://github.com/android/compose-samples`
>    - Android Snippets: `https://github.com/android/snippets`

**실행 순서:**

1. **[필수]** `WebFetch`로 공식 문서 fetch → 내용 분석
2. **[필수]** `WebFetch`로 NowInAndroid / JetNews 관련 코드 fetch → 실제 사용 패턴 확인
3. 공식 문서 + 샘플 코드를 종합하여 핵심 개념 3~5가지 설명
4. 설명마다 "공식 문서에서는 ~", "NowInAndroid에서는 ~" 출처 명시
5. `AskUserQuestion`으로 이해도 확인:
   - "이해됐어요" → 다음으로
   - "이 부분 다시 설명해줘" → 해당 개념 보완 설명 후 재확인 (핑퐁 반복)

**Phase 1 완료 시:**
- `study/plan/<topic>/README.md` 개념 섹션 업데이트 (출처 링크 포함)
- STUDY_BACKLOG.md 상태 → "진행 중 🔄 (Phase 2)"
- 커밋:
  ```
  study: teach <topic>

  - 공식 문서 기반 핵심 개념 정리
  - <학습한 핵심 개념 1~3줄 요약>
  ```

**규칙:**
- 공식 문서와 샘플 코드를 fetch하지 않은 상태에서 설명 시작 금지
- 한 번에 하나의 개념만 설명
- 코드 예제는 반드시 공식 문서 또는 우수 샘플에서 가져온 것 사용
- "왜 이렇게 쓰는가" 이유 설명 필수
- 출처 항상 명시 (어느 문서/샘플의 어느 부분인지)

---

### Phase 2: SAMPLE (베스트 프랙티스)

**목표:** 실제 프로덕션 코드에서 어떻게 사용하는지 보여준다.

1. NowInAndroid / JetNews / Android Snippets 참조 코드 제시
2. 패턴 설명 (왜 이렇게 구현했는가)
3. 테스트 코드까지 포함해서 제시
4. `AskUserQuestion`: "이 패턴에서 핵심이 뭐라고 생각해?"

**Phase 2 완료 시:**
- `study/plan/<topic>/README.md` 베스트 프랙티스 섹션 업데이트
- STUDY_BACKLOG.md 상태 → "진행 중 🔄 (Phase 3)"
- 커밋:
  ```
  study: sample <topic>

  - NowInAndroid / JetNews 참조 패턴 정리
  - <핵심 패턴 1~2줄 요약>
  ```

**참고 링크:**
- NowInAndroid: `https://github.com/android/nowinandroid`
- JetNews: `https://github.com/android/compose-samples`
- Android Snippets: `https://github.com/android/snippets`

---

### Phase 3: CHALLENGE (코딩 챌린지)

**목표:** 학습 내용을 직접 구현해본다.

1. 구현 문제 제시 (Codelab 스타일):
   - 문제 설명
   - 요구사항 명세
   - 힌트 (선택적)

2. PSM으로 정답 worktree 생성 (백그라운드):
   ```
   /psm feature <project> answer/<topic-name>
   ```
   사용자가 푸는 동안 백그라운드에서 정답 코드 + 테스트 작성

3. `AskUserQuestion`: 구현 완료 여부 확인
   - "다 구현했어" → Phase 4로
   - "막혔어" → 힌트 제공 후 대기

**Phase 3 완료 시 (사용자 구현 완료 후):**
- STUDY_BACKLOG.md 상태 → "진행 중 🔄 (Phase 4)"
- 커밋 (사용자 코드):
  ```
  study: challenge <topic>

  - <문제 요약>
  - 구현 완료, 리뷰 전
  ```

---

### Phase 4: REVIEW (코드 리뷰)

**목표:** 내 코드와 정답 코드를 비교하며 개선점을 파악한다.

1. 사용자 코드 읽기
2. 정답 worktree 코드 읽기
3. 상세 리뷰 제공:
   - 잘한 점
   - 개선할 수 있는 점 (이유 포함)
   - 더 나은 패턴 제안
4. `AskUserQuestion`:
   - "다음 토픽으로" → 완료 처리 후 Phase 1 재시작
   - "이 부분 더 파고들기" → 추가 설명 후 재확인
   - "오늘은 여기서 끝" → 진행 중 상태 유지 후 종료

**Phase 4 완료 시:**
- `study/plan/<topic>/README.md` 핵심 정리 섹션 업데이트
- STUDY_BACKLOG.md: 진행 중 → 완료 ✅ (완료일: YYYY-MM-DD)
- 커밋:
  ```
  study: complete <topic>

  - 4단계 학습 사이클 완료
  - <이번 학습에서 얻은 핵심 인사이트 1~2줄>
  ```

---

## STUDY_BACKLOG.md 상태값

| 상태 | 의미 |
|------|------|
| `학습 계획 완료` | README 작성됨, 아직 시작 전 |
| `진행 중 🔄 (Phase 1)` | 공식 문서 학습 중 |
| `진행 중 🔄 (Phase 2)` | 베스트 프랙티스 학습 중 |
| `진행 중 🔄 (Phase 3)` | 코딩 챌린지 구현 중 |
| `진행 중 🔄 (Phase 4)` | 코드 리뷰 중 |
| `완료 ✅` | 전체 사이클 완료 |

---

## 커밋 컨벤션 요약

```
study: teach <topic>      # Phase 1 완료 — 개념 정리
study: sample <topic>     # Phase 2 완료 — 베스트 프랙티스
study: challenge <topic>  # Phase 3 완료 — 챌린지 구현
study: complete <topic>   # Phase 4 완료 — 전체 사이클
```

---

## 코드 스타일 규칙

- Kotlin trailing comma 사용
- 함수에 명시적 반환 타입 선언
- 테스트: JVM 모듈은 JUnit5+Kotest, Android 모듈은 Robolectric
- 커밋 전 `./gradlew ktlintFormat` 실행

---

## 참고

- STUDY_BACKLOG.md: 프로젝트 루트의 `STUDY_BACKLOG.md`
- 공식 문서: `https://developer.android.com`
- NowInAndroid: `https://github.com/android/nowinandroid`
- Compose Samples: `https://github.com/android/compose-samples`
