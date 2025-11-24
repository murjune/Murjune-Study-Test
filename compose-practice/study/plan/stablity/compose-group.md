# Movable, Replaceable, Restartable 그룹

Compose 컴파일러는 각 Composable 함수를 분석해서 세 가지 그룹 타입 중 하나로 분류합니다:

### **Restartable** (재시작 가능)

```kotlin
@Composable
fun Counter(count: Int) {  // Restartable
    Text("Count: $count")
}
```

- 상태 변경 시 **해당 Composable만 독립적으로 재구성**할 수 있음
- 가장 일반적인 타입
- Compose가 recomposition scope를 생성하여 필요한 부분만 다시 실행

### **Skippable** (건너뛰기 가능)

```kotlin
@Composable
fun Greeting(name: String) {  // Restartable + Skippable
    Text("Hello, $name")
}

// 사용
Greeting("June")  // name이 변경되지 않으면 recomposition 스킵
```

- 모든 파라미터가 변경되지 않았으면 recomposition을 **완전히 건너뜀**
- 파라미터가 모두 stable하거나 immutable할 때 적용
- Restartable과 함께 적용됨

### **Movable** (이동 가능)

```kotlin
@Composable
fun ItemList(items: List<Item>) {
    items.forEach { item ->
        ItemRow(item)  // Movable
    }
}
```

- 리스트 재정렬 시 **UI 트리에서 위치만 변경**
- 불필요한 재생성을 방지
- `key()`와 함께 사용하면 더 효과적

```kotlin
LazyColumn {
    items(items, key = { it.id }) { item ->  // key로 identity 제공
        ItemRow(item)  // 이제 안전하게 movable
    }
}
```

### **Replaceable** (교체 가능)

```kotlin
@Composable
fun Content(showA: Boolean) {
    if (showA) {
        ComponentA()  // Replaceable
    } else {
        ComponentB()  // Replaceable
    }
}
```

- 조건부 로직에서 **완전히 다른 컴포넌트로 교체**
- 이전 컴포넌트는 완전히 dispose되고 새 컴포넌트가 생성됨
- Movable의 반대 개념

## 2. @Stable과의 연관성

**@Stable은 Skippable 최적화의 핵심입니다:**

### Stable하지 않은 경우

```kotlin
// Stable하지 않은 경우
data class User(
    var name: String,  // mutable
    var age: Int
)

@Composable
fun UserCard(user: User) {  // Restartable이지만 Skippable 아님
    Text("${user.name}, ${user.age}")
}

// 사용
val user = User("June", 25)
UserCard(user)  // user 객체가 같아도 매번 recomposition
```

### @Stable 적용

```kotlin
// @Stable 적용
@Stable
data class User(
    val name: String,  // immutable
    val age: Int
)

@Composable
fun UserCard(user: User) {  // Restartable + Skippable
    Text("${user.name}, ${user.age}")
}

// 사용
val user = User("June", 25)
UserCard(user)  // user가 변경되지 않으면 스킵!
```

### **@Stable 조건**

1. 같은 입력 → 같은 결과 (referential equality)
2. 공개 프로퍼티 변경 시 Compose에 알림
3. 모든 공개 프로퍼티가 stable

### **StateFlow와의 관계**

```kotlin
// MutableStateFlow는 @Stable
@Stable
interface StateFlow<out T> : Flow<T> {
    val value: T
}

@Composable
fun Counter() {
    val count by viewModel.uiState.collectAsState()  // Stable

    // count가 변경되지 않으면 스킵
    CounterDisplay(count)
}

@Composable
fun CounterDisplay(count: Int) {  // Int는 stable → Skippable
    Text("Count: $count")
}
```

## 정리

| 그룹 타입       | 의미           | @Stable 관련성     |
|-------------|--------------|-----------------|
| Restartable | 독립적 재구성 가능   | 기본 속성           |
| Skippable   | 파라미터 불변 시 스킵 | **@Stable이 핵심** |
| Movable     | 위치 변경만 수행    | Identity 보존     |
| Replaceable | 완전 교체        | 조건부 UI          |

### **@Stable의 역할**

- Skippable 최적화를 가능하게 함
- StateFlow처럼 상태 변경을 추적할 수 있는 타입에 적용
- 불필요한 recomposition 방지의 핵심

이것이 StateFlow의 `update`를 사용할 때 Compose에서 효율적으로 recomposition이 일어나는 이유입니다! 🎯
