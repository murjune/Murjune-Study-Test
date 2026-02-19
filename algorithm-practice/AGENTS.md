<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-02-19 | Updated: 2026-02-19 -->

# algorithm-practice

## Purpose
알고리즘과 자료구조를 학습하고 구현하는 모듈. 조합론, 다익스트라, 플로이드-워셜, LCA(최소 공통 조상), BST(이진 탐색 트리) 등의 알고리즘을 Java/Kotlin으로 구현하고 테스트로 검증한다.

## Key Files

| File | Description |
|------|-------------|
| `build.gradle.kts` | 모듈 빌드 설정 |

## Subdirectories

| Directory | Purpose |
|-----------|---------|
| `src/main/java/com/murjune/practice/algorithm/` | 알고리즘 구현 소스 |
| `src/test/kotlin/com/murjune/practice/algorithm/` | 알고리즘 학습 테스트 |

## Source Layout — main

| Package | Description |
|---------|-------------|
| `algorithm/combination/` | 조합(Combination) 알고리즘 구현 |
| `algorithm/common/` | 공통 유틸리티 |
| `algorithm/datastructure/bst/` | 이진 탐색 트리(BST) 구현 (이미지 포함) |
| `algorithm/dijkstra/` | 다익스트라 최단 경로 알고리즘 |
| `algorithm/floyd/` | 플로이드-워셜 알고리즘 |
| `algorithm/lca/` | 최소 공통 조상(LCA) 알고리즘 |

## Source Layout — test

| Package | Description |
|---------|-------------|
| `algorithm/combination/` | 조합 알고리즘 테스트 |
| `algorithm/datastructure/bst/` | BST 테스트 |
| `algorithm/dijkstra/` | 다익스트라 테스트 |
| `algorithm/lca/` | LCA 테스트 |

## For AI Agents

### Working In This Directory
- 구현은 `src/main/java`(Java 또는 Kotlin 혼용), 테스트는 `src/test/kotlin`.
- 알고리즘 파일 추가 시 해당 알고리즘 패키지 아래에 넣는다.
- 복잡한 자료구조에는 이미지/다이어그램을 `image/` 서브디렉토리에 첨부할 수 있다.

### Testing Requirements
```bash
./gradlew :algorithm-practice:test
```

### Common Patterns
- JUnit5 기반 테스트
- 엣지 케이스(빈 그래프, 단일 노드 등) 반드시 테스트

## Dependencies

### External
- JUnit5, Kotest

<!-- MANUAL: Any manually added notes below this line are preserved on regeneration -->
