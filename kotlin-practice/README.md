# Kotlin Practice

Kotlin stdlib 기본 문법을 학습테스트 방식으로 탐구하는 모듈입니다.

- Test Tool: JUnit5, Kotest
- main() 함수로 학습하던 코드를 test 코드로 마이그레이션 중

---

## 완료한 학습

| 주제 | 테스트 위치 |
|------|------------|
| Sequence (generateSequence) | `src/test/kotlin/.../sequence/` |
| Kotlin Serialization | `src/test/kotlin/.../serialization/` — [README](./src/test/kotlin/com/murjune/practice/serialization/README.md) |

---

## 백로그

| 주제 | 우선순위 | 메모 |
|------|----------|------|
| Sequence vs Collection 성능 비교 | 중 | generateSequence 일부 완료 |
| Delegation (by 키워드) | 중 | |
| Kotlin DSL 빌더 패턴 | 낮음 | |
| Serialization 심화 | 낮음 | |

→ 전체 백로그: [STUDY_BACKLOG.md](../STUDY_BACKLOG.md)

---

## 테스트 실행

```bash
./gradlew :kotlin-practice:test
```
