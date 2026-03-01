# Navigation Challenge 학습 노트

## currentDestination 안전하게 가져오기 패턴

### 문제: NavController는 Compose 상태가 아님

```kotlin
// Compose에서 이렇게 쓰면 안 됨
navController.currentDestination
```
- NavController는 상태 객체가 아니라 **변경 감지가 안 됨** (recomposition 안 됨)

### 해결: Flow + 이전값 캐싱

```kotlin
@Stable
data class NavigationChallengeStates(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            val currentEntry = navController.currentBackStackEntryFlow
                .collectAsState(initial = null)

            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }
}
```

### 왜 `previousDestination` 캐싱이 필요한가?

Navigation 전환 순간에 destination이 **잠깐 null이 되는 타이밍**이 존재함:
- 그래프 초기화 중
- recomposition 타이밍
- back stack 변경 중

```
화면 A → B 이동 시 내부 흐름:
t0: destination = A
t1: destination = null   ← 전환 중 (순간적)
t2: destination = B
```

| | 캐싱 없음 | 캐싱 있음 |
|---|---|---|
| 흐름 | A → null → B | A → A → B |
| 결과 | UI 깜빡임, 탭 상태 튐 | 안정적 |

### 핵심 포인트

- `currentBackStackEntryFlow`: 현재 화면 변경을 Compose에서 관찰 가능한 Flow
- `collectAsState(initial = null)`: Flow → Compose State 변환, 초기값 null
- `also` 블록: 현재 값이 있으면 저장
- `?: previousDestination.value`: 현재 값이 null이면 마지막 정상값 반환
- `previousDestination`이 `MutableState`인 이유: Compose recomposition 트리거

---

## hierarchy와 hasRoute

### `NavDestination.hierarchy`

```kotlin
public val NavDestination.hierarchy: Sequence<NavDestination>
    get() = generateSequence(this) { it.parent }
```
- 현재 destination부터 부모 NavGraph를 거슬러 올라가는 시퀀스
- 예: `ProductDetail → HomeNavGraph → RootGraph`

### `NavDestination.hasRoute<T>()`

```kotlin
public inline fun <reified T : Any> NavDestination.hasRoute(): Boolean =
    hasRoute(T::class)

public fun <T : Any> NavDestination.hasRoute(route: KClass<T>): Boolean =
    route.serializer().generateHashCode() == id
```
- `@Serializable` Route의 serializer 해시코드와 destination id를 비교
- 정확히 해당 Route 타입인지 판별

### 탭 판별 시 hierarchy가 필요한 이유

```kotlin
// hasRoute만 쓰면: 탭 화면 자체에서만 동작
currentDestination?.hasRoute<HomeRoute>() == true
// HomeScreen에서만 true, ProductDetail에서는 false!

// hierarchy + hasRoute: 하위 화면에서도 동작
currentDestination?.hierarchy?.any { it.hasRoute<HomeRoute>() } == true
// ProductDetail → HomeNavGraph에 HomeRoute 있으므로 true!
```

| 현재 화면 | hasRoute만 | hierarchy + hasRoute |
|-----------|-----------|---------------------|
| HomeScreen | true | true |
| ProductDetail | false | true (부모 탐색) |
| Review | false | true (부모 탐색) |

**전제조건**: Home 하위 화면들이 Home 중첩 NavGraph 안에 있어야 함
