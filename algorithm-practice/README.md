# Algorithm Practice

알고리즘/자료구조의 기본 개념을 학습하고 테스트로 검증하는 모듈입니다.

- Test Tool: JUnit5, Kotest
- 참고: https://github.com/murjune/today_junelog 에서 정리했던 내용을 마이그레이션 중

---

## 완료한 학습

### 알고리즘

#### 순열 & 조합
- [조합 구현](./src/main/java/com/murjune/practice/algorithm/combination)

#### 최단 경로
- [다익스트라 알고리즘](./src/main/java/com/murjune/practice/algorithm/dijkstra) — 우선순위 큐 기반 단일 출발지 최단 경로
- [플로이드-와샬 알고리즘](./src/main/java/com/murjune/practice/algorithm/floyd) — 모든 쌍 최단 경로 (O(V³))

### 자료구조

#### BST (Binary Search Tree)
- [Red-Black Tree 구현](./src/main/java/com/murjune/practice/algorithm/datastructure/bst/RedBlackTree.kt)
- [Red-Black Tree 이론](./src/main/java/com/murjune/practice/algorithm/datastructure/bst/RedBlackTree.md)
- [Red-Black Tree 삽입](./src/main/java/com/murjune/practice/algorithm/datastructure/bst/RedBlackTree_Insert.md)
- [Red-Black Tree 삭제](./src/main/java/com/murjune/practice/algorithm/datastructure/bst/RedBlackTree_Delete.md)
- [트리 회전](./src/main/java/com/murjune/practice/algorithm/datastructure/bst/TreeRotate.md)

---

## 백로그

| 주제 | 우선순위 |
|------|----------|
| BFS / DFS | 높음 |
| 동적 프로그래밍 (DP) | 높음 |
| 이분 탐색 | 중 |
| 위상 정렬 | 낮음 |

→ 전체 백로그: [STUDY_BACKLOG.md](../STUDY_BACKLOG.md)

---

## 테스트 실행

```bash
./gradlew :algorithm-practice:test

# 특정 테스트
./gradlew :algorithm-practice:test --tests "*.DijkstraTest"
./gradlew :algorithm-practice:test --tests "*.RedBlackTreeTest"
```
