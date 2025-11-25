# LazyStaggeredGrid 이미지 로딩 성능 최적화

## 문제 상황

### 1. Layout Thrashing (가장 심각)

**문제**: LazyVerticalStaggeredGrid에서 AsyncImage가 이미지를 로드할 때마다 레이아웃이 출렁이는 현상

**원인**:
- AsyncImage가 이미지를 로드하기 전까지는 아이템의 높이를 모름
- 이미지가 로드될 때마다 그리드 전체가 재측정(remeasure)되고 재배치(relayout)됨
- 100개의 이미지가 순차적으로 로드되면 100번의 레이아웃 재계산 발생

### 2. 초기 높이 0px 문제

이미지가 비동기로 로드되기 전에는 아이템 높이가 0px이라서, Lazy layout이 뷰포트에 모든 아이템을 컴포즈하려고 시도함

### 3. 메모리 과다 사용

원본 고해상도 이미지(4K, 8K)를 그대로 메모리에 올리면 수백 MB의 메모리를 소비하며 스크롤 래깅 발생

### 4. 불필요한 리컴포지션

스크롤 시 동일한 아이템이 반복적으로 재구성됨

> This can happen in scenarios where, for example, you expect to asynchronously retrieve some data like images, to fill your list's items at a later stage. That would cause the Lazy layout to compose all of its items in the first measurement, as their height is 0 pixels and it could fit them all in the viewport. Once the items have loaded and their height expanded, Lazy layouts would then discard all of the other items that have unnecessarily been composed the first time around as they cannot in fact fit the viewport. To avoid this, you should set default sizing to your items, so that the Lazy layout can do the correct calculation of how many items can in fact fit in the viewport.

**번역**:
예를 들어 이미지와 같은 데이터를 비동기로 가져와서 나중에 리스트의 아이템을 채우는 시나리오에서 이런 일이 발생할 수 있습니다. 아이템의 높이가 0픽셀이고 모두 뷰포트에 들어갈 수 있기 때문에, Lazy 레이아웃은 첫 번째 측정에서 모든 아이템을 컴포즈하게 됩니다. 아이템이 로드되고 높이가 확장되면, Lazy 레이아웃은 실제로는 뷰포트에 맞지 않는 불필요하게 컴포즈된 다른 모든 아이템들을 폐기합니다. 이를 방지하려면 아이템에 기본 크기를 설정하여 Lazy 레이아웃이 실제로 뷰포트에 맞을 수 있는 아이템 수를 올바르게 계산할 수 있도록 해야 합니다.

---

## 해결 방법

### 1. AspectRatio 지정 (가장 중요! ⭐)

**Layout Thrashing을 막는 핵심 해결책**

이미지의 **종횡비(Aspect Ratio)**를 미리 지정하여 이미지 로딩 전에 아이템이 차지할 공간을 확보합니다.

```kotlin
AsyncImage(
    model = photo,
    contentDescription = null,
    modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(width.toFloat() / height.toFloat()), // 핵심!
)
```

**효과**:
- 이미지 로딩 전에 레이아웃 크기가 확정됨
- 그리드 전체가 재측정되지 않음
- 스크롤이 부드러워짐

**예시**: 이미지가 800x1200px이면 `aspectRatio(800f / 1200f)` = 0.667 비율로 공간을 미리 확보

---

### 2. Coil 다운샘플링 (메모리 최적화)

**다운샘플링(Downsampling)**: 원본 고화질 이미지를 화면에 필요한 크기로 줄여서 메모리에 올리는 과정

```kotlin
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data(photo)
        .size(200) // 200x200 픽셀 박스를 목표로 함
        .precision(Precision.INEXACT) // 비율은 깨지 않도록 허용
        .build(),
    modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(0.75f),
)
```

**파라미터 설명**:
- **`size(200)`**: 이미지를 200px 크기로 다운샘플링. 원본이 4000x3000px이어도 200x150px 정도로 축소
- **`Precision.INEXACT`**: 정확히 200px이 아니어도 괜찮음. 비율(aspect ratio)은 유지하면서 적당한 크기로 조정
- **`Precision.EXACT`**: 정확히 200x200px로 맞춤 (비율이 깨질 수 있음)

**효과**: 원본 해상도의 **수백 분의 일** 크기인 가벼운 비트맵만 메모리에 올림 → 메모리 사용량 대폭 감소

---

### 3. AsyncImage 추가 최적화

```kotlin
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data(photo)
        .crossfade(true) // 부드러운 전환 효과
        .memoryCachePolicy(CachePolicy.ENABLED) // 메모리 캐시 활성화
        .diskCachePolicy(CachePolicy.ENABLED) // 디스크 캐시 활성화
        .build(),
    contentScale = ContentScale.Crop,
    contentDescription = "Random image",
    placeholder = ColorPainter(Color.LightGray), // 로딩 중 placeholder
    error = ColorPainter(Color.Red), // 에러 시 표시
    modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
)
```

**주요 파라미터 설명**:
- **`crossfade(true)`**: 이미지 로딩 완료 시 페이드 인 애니메이션으로 부드럽게 전환 (placeholder → 실제 이미지)
- **`size`**: 이미지 크기 지정. `Size.ORIGINAL`은 원본 크기를 사용하지만, StaggeredGrid에서는 성능 문제가 있을 수 있음. 차라리 생략하거나 적절한 픽셀 크기 지정
- **`placeholder`**: 이미지 로딩 중 표시할 컴포저블 (빈 공간 대신 색상이나 로딩 인디케이터)
- **`memoryCachePolicy` / `diskCachePolicy`**: Coil의 캐싱 정책 활성화

### 2. Key 파라미터 추가 (필수!)

```kotlin
LazyVerticalStaggeredGrid(
    columns = StaggeredGridCells.Fixed(2),
    // ...
) {
    items(
        items = photos,
        key = { it }, // 리컴포지션 최적화
    ) { photo ->
        AsyncImage(...)
    }
}
```

**효과**: 스크롤 시 불필요한 리컴포지션 방지. 동일한 아이템이 재사용됨.

---

### 5. Coil 캐싱 전략

Coil은 기본적으로 **메모리 캐시**와 **디스크 캐시**를 모두 활성화하며, 별도 설정 없이도 효율적으로 작동합니다.

**기본 설정**:
- 메모리 캐시: 앱 가용 메모리의 약 **25%** 자동 사용
- 디스크 캐시: 자동 관리

대부분의 경우 기본 설정으로 충분하지만, 필요시 커스터마이징 가능합니다.

#### Coil ImageLoader 전역 설정 (Application 클래스)

```kotlin
class MyApplication : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25) // 디바이스 메모리의 25% 사용
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(512L * 1024 * 1024) // 512MB
                    .build()
            }
            .build()
    }
}
```

**효과**: 앱 전체에서 이미지 캐싱이 효율적으로 작동. 메모리와 디스크 캐시 크기 조정 가능.

---

## 추가 최적화 팁

1. **특정 크기 지정**: 가로/세로 크기를 따로 지정하고 싶을 때
   ```kotlin
   .size(width = 500, height = 800)
   ```

2. **LazyStaggeredGrid의 prefetchCount 조정**: 미리 로드할 아이템 수 증가 (기본값보다 늘림)

3. **contentScale 최적화**:
   - `ContentScale.Crop`: 이미지를 잘라서 영역 채움 (기본값)
   - `ContentScale.Fit`: 이미지 전체를 보여주되 비율 유지
   - `ContentScale.Inside`: 원본보다 작게만 조정

---

## 성능 개선 효과

### AspectRatio 지정 효과
- ✅ **Layout Thrashing 완전 해결** - 레이아웃 재계산 100번 → 0번
- ✅ 스크롤이 훨씬 부드러워짐
- ✅ 초기 로딩 시 화면 출렁임 없음

### 다운샘플링 효과
- ✅ **메모리 사용량 수백 분의 일로 감소** - 4000x3000px → 200x150px
- ✅ 스크롤 래깅 대폭 감소
- ✅ OOM(Out Of Memory) 방지

### 캐싱 효과
- ✅ 불필요한 네트워크 요청 방지
- ✅ 재방문 시 즉시 로딩

### Key 파라미터 효과
- ✅ 불필요한 리컴포지션 방지
- ✅ 아이템 재사용으로 성능 향상

---

## 핵심 요약

**가장 중요한 3가지**:
1. ⭐ **AspectRatio 지정** - Layout Thrashing 해결
2. ⭐ **size + Precision.INEXACT** - 메모리 최적화
3. ⭐ **key 파라미터** - 리컴포지션 최적화