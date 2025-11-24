💡 전제: @Stable은 “이 타입은 안정적이며 변경이 감지될 수 있다”는 약속

@Stable 타입은 다음을 보장한다고 컴파일러는 믿고 최적화를 수행합니다:

1. 내부 값이 동일하다면 UI를 다시 그릴 필요가 없다.
2. 값이 바뀌면 Compose가 인지하고 리컴포지션을 일으킨다.

---

🧩 그런데? 내부 프로퍼티가 Unstable 타입이라면

예를 들어, 다음처럼 @Stable만 붙여두고,
안에 있는 타입이 안정성을 전혀 보장하지 않는 경우를 생각해봅시다:

```kotlin
@Stable
data class Wrapper(val data: MutableList<String>)
```

- Wrapper는 왜인지 @Stable로 선언됨
- 내부 프로퍼티 data는 MutableList
- MutableList는 Unstable이고 변경을 Compose에 알려주지 않음

---
🔍 Compose 입장에서 보면
컴파일러는 이렇게 오해할 수 있습니다:

“Wrapper는 @Stable이니까 내부 값이 바뀌면 UI를 다시 렌더링할 수 있는 구조겠지!”

하지만 실제로는:
• data.add("hello")처럼 내부 값이 바뀌어도 Compose는 그 변경 사실을 전혀 모릅니다.
• 그러면? 변경이 발생했는데도 리컴포지션이 일어나지 않음 → UI 업데이트 누락

---
🧨 그래서 생기는 문제

프로퍼티가 Unstable인데, Stable로 표시하는 경우,
Compose는 내부 속성을 추적할 수 없으니까 최적화를 잘못 적용하게 됩니다.

---

❌ 예: 예상 불가한 리컴포지션 스킵

```kotlin
@Stable
data class Foo(val items: MutableList<Int>)

@Composable
fun Example(foo: Foo) {
    Text(text = "Items count: ${foo.items.size}")
}
```

	•	foo.items.add(100)이 실행되어도
	•	Compose는 Foo가 안정적이라고 믿고 리컴포지션을 생략
	•	화면엔 여전히 Items count: 3 이런 식으로 표시됨

---
🔑 요약
| 케이스 | Compose가 예상하는 상태 | 실제 상태 | 결과 |
|---------------------------------|------------------------------|------------------------|--------------------|
| @Stable 타입 + 내부도 Stable | 내부 변경 → 감지됨 | 일치 | 🟢 정상 |
| @Stable 타입 + 내부는 Unstable | 내부 변경 → 감지됨 | 🚨 변경 감지 안 됨 | ❌ UI stale |
| Unstable 타입 | 내부 변경 → 감지 안 됨 | 일치 | 😐 but no optimization |

📝 핵심 메시지:

@Stable은 “내부까지 안전하게 Compose가 추적할 수 있어”라는 약속입니다.
그 약속을 지키지 않으면, Compose는 건너뛰어도 되는 곳에서 건너뛰지 말아야 할 리컴포지션을 스킵합니다.
