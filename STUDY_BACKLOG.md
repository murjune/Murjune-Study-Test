# 학습 백로그

이 프로젝트의 전체 학습 항목을 모듈별로 관리합니다.
`/study` skill과 연동되어 학습 진행 상태를 자동으로 업데이트합니다.

**우선순위: compose-practice > android-libs-practice > coroutine > kotlin-practice**

---

## 진행 중

| 모듈 | 주제 | 상태 | 링크 |
|------|------|------|------|
| compose-practice | Navigation 2 (NavHost, Type-Safe, BackStack, DeepLink) | 진행 중 🔄 (Phase 1) | [README](compose-practice/src/main/java/com/murjune/pratice/compose/study/sample/navigation/README.md) |

---

## 백로그 (예정)

### compose-practice ⭐ 최우선
| 주제 | 우선순위 | 메모 |
|------|----------|------|
| Navigation 3 | 높음 | 🆕 2025.11 stable. BackStack을 개발자가 직접 소유(SnapshotStateList), NavDisplay, Navigation 2 완료 후 |
| retain API (retain {}, RetainedEffect) | 높음 | 🆕 2025.12 신규. remember↔rememberSaveable 사이 새 상태 primitive. config change 생존, process death 불가. Flow/람다/비트맵 등 비직렬화 객체 유지 |
| Shared Element Transition | 높음 | 🆕 1.10 stable. SharedTransitionLayout, sharedElement modifier, skipToLookaheadPosition, initialVelocity 지원 |
| Predictive Back Gesture + Compose | 높음 | 🆕 Android 14+ in-app predictive back. OnBackPressedCallback, Progress API로 커스텀 애니메이션 |
| Pausable Composition & Lazy Prefetch 성능 | 중 | 🆕 2025.12 BOM 기본 활성화. 런타임이 composition 일시중지/재개, 스크롤 성능 View 수준 달성 |
| SideEffect (LaunchedEffect, SideEffect, DisposableEffect) | 높음 | |
| `inline` vs `noinline` 람다 최적화 | 중 | |
| Paging3 + LazyColumn | 중 | |
| Stable/Immutable 테스트 코드 직접 검증 | 낮음 | |

### compose-ui-practice
| 주제 | 우선순위 | 메모 |
|------|----------|------|
| Animation (AnimatedVisibility, Transition, Animatable) | 중 | |
| Custom Layout (Layout, SubcomposeLayout) | 중 | |
| Canvas & Drawing | 낮음 | |

### android-architecture 🏗️ 최신 아키텍처 API
| 주제 | 우선순위 | 메모 |
|------|----------|------|
| SavedStateHandle 신규 API (getMutableStateFlow, saved delegate, KotlinX Serialization 연동) | 높음 | 🆕 process death 대응. nullable 지원 추가 |
| CreationExtras 개선 (builder factory, map-like operator overloads) | 중 | 🆕 ViewModel Factory에서 Kotlin-idiomatic하게 사용 |

### android-libs-practice
| 주제 | 우선순위 | 메모 |
|------|----------|------|
| Intent / PendingIntent | 높음 | DeepLink 연계 |
| Notification.ProgressStyle (Android 16) | 중 | 🆕 Android 16 신규. 배달/내비게이션 등 진행 상황 중심 알림 스타일 |
| ContentProvider | 중 | Uri 학습과 연계 |
| BroadcastReceiver | 낮음 | |

### coroutine
| 주제 | 우선순위 | 메모 |
|------|----------|------|
| CancellableContinuation 심화 | 중 | 기본 테스트는 완료 |
| Flow Cold vs Hot 정리 문서화 | 낮음 | 테스트는 있으나 README 미작성 |

### kotlin-practice
| 주제 | 우선순위 | 메모 |
|------|----------|------|
| Kotlin DSL 빌더 패턴 | 낮음 | |

---

## 완료

### compose-practice
| 주제 | 완료일 |
|------|--------|
| Compose Stability (@Stable, @Immutable, Compose Group) | - |
| LazyColumn / LazyGrid / StickyHeader / 이미지 로드 성능 | - |

### android-libs-practice
| 주제 | 완료일 |
|------|--------|
| Android Uri (파싱, 빌더, UriMatcher, File vs Content URI) | - |

### coroutine
| 주제 | 완료일 |
|------|--------|
| 코루틴 기초 (suspend, CoroutineScope, BackgroundScope) | - |
| Job / SupervisorJob / Exception 전파 | - |
| CoroutineCancel 주의사항 | - |
| CancellableContinuation 기초 | - |
| CAS (Compare-And-Swap) / SpinLock | - |
| Channel 기초 (send/receive, produce, trySend) | - |
| Channel as Flow / ChannelStateHolder | - |
| Channel vs SharedFlow 비교 | - |
| Flow 예외 처리 (Flow, SharedFlow) | - |
| Flow 연산자 (flatMapLatest/Merge/Concat, combine, debounce, merge, zip, onStart) | - |
| SharedFlow 연산자 | - |
| StateFlow / MutableStateFlow | - |
| EventFlow (단발 이벤트 처리) | - |
| SharedVariable 동시성 | - |
| ViewModel 코루틴 테스트 | - |

### kotlin-practice
| 주제 | 완료일 |
|------|--------|
| Annotation + Reflection 연계 | - |
| Class 로딩 동작 | - |
| Closure (변수 캡처) | - |
| Collection (ArrayDeque, ImmutableCollection, PriorityQueue, reduce/fold) | - |
| Delegate 프로퍼티 (by 키워드) | - |
| LocalDate API | - |
| MockK (기본, 어노테이션, 계층, object, 오버라이드, spyk, inject) | - |
| Reflection (생성자, 함수, 프로퍼티, KType, 커스텀 JSON 파서) | - |
| Regex (기본, 실용 예제) | - |
| Sequence (generateSequence, 성능 비교) | - |
| Kotlin Serialization (기본, 주의사항, Gson 비교, Json 옵션, 커스텀 Serializer) | - |
| stdlib (Char, CharArray, 컬렉션 변환, Comparable) | - |

---

## 학습 방식 (`/study` skill)

```
/study                    # 백로그 확인 후 토픽 자동 선택
/study "주제명"            # 특정 주제 바로 시작
/study --next             # 다음 백로그 항목으로
/study --status           # 진행 상태 확인
```

**4단계 사이클:**
1. **TEACH** — [필수] 공식 문서 + 우수 샘플 fetch 후 설명 + 핑퐁 Q&A
2. **SAMPLE** — NowInAndroid / JetNews 베스트 프랙티스 + 테스트 코드
3. **CHALLENGE** — 코딩 문제 제시 (백그라운드에서 정답 worktree 생성)
4. **REVIEW** — 내 코드 vs 정답 코드 비교 리뷰

각 Phase 완료 시: STUDY_BACKLOG.md 상태 업데이트 + `study: <phase> <topic>` 커밋

---

## 모듈별 테스트 실행

```bash
./gradlew :compose-practice:test
./gradlew :compose-ui-practice:testDebugUnitTest
./gradlew :coroutine:test
./gradlew :kotlin-practice:test
./gradlew :android-libs-practice:test
```

---

## 참고

- [NowInAndroid](https://github.com/android/nowinandroid)
- [Jetpack Compose Samples](https://github.com/android/compose-samples)
- [Android Snippets](https://github.com/android/snippets)
- [Android 공식 문서](https://developer.android.com)
