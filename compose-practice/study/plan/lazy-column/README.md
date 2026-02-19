# LazyColumn / LazyGrid 학습

Compose의 지연 목록 컴포넌트(LazyColumn, LazyRow, LazyGrid)를 학습합니다.

- 공식 문서: https://developer.android.com/develop/ui/compose/lists
- 공식 GitHub 스니펫: https://github.com/android/snippets/blob/main/compose/snippets/src/main/java/com/example/compose/snippets/lists/LazyListSnippets.kt

---

## 학습 내용

### 1. LazyColumn 기본 (`LazyListSample.kt`)

스크롤 가능한 목록을 효율적으로 렌더링하는 LazyColumn의 기본 사용법을 학습합니다.

**핵심 특징:**
- 화면에 보이는 항목만 컴포즈 (RecyclerView와 유사)
- `items()`, `item()`, `itemsIndexed()` DSL로 목록 구성

```kotlin
LazyColumn {
    item { Header() }
    items(dataList) { item ->
        ItemRow(item)
    }
    item { Footer() }
}
```

---

### 2. Sticky Header (`StickyHeaderSample.kt`)

`stickyHeader`를 사용해 스크롤 시 상단에 고정되는 헤더를 구현합니다.

```kotlin
LazyColumn {
    stickyHeader { SectionHeader("Section A") }
    items(sectionItems) { ItemRow(it) }
}
```

---

### 3. LazyGrid / LazyStaggeredGrid (`LazyGridSample.kt`, `LazyStaggeredGridImageSample.kt`)

격자형 목록과 엇갈린 격자형 목록을 학습합니다.

```kotlin
// 고정 열 수
LazyVerticalGrid(columns = GridCells.Fixed(2)) { ... }

// 최소 크기 기반 동적 열 수
LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) { ... }

// 엇갈린 격자 (이미지 갤러리 등)
LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2)) { ... }
```

---

## 참고 자료

- [LazyColumn 작동 방식 이해하기 (레아 글)](https://wisemuji.medium.com/lazycolumn-%EC%9E%91%EB%8F%99-%EB%B0%A9%EC%8B%9D-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0-0a5433f31306)
- [이미지 로드 성능](./image-load-performance.md)

---

## 다음 학습 (TODO)

- [ ] Paging3 with LazyColumn
