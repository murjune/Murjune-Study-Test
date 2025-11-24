🔍 Compose에서 파라미터 비교는 이렇게 이루어집니다:
1.	참조 비교 (===)
      → 먼저 이전 호출의 파라미터와 같은 객체인지 확인합니다.
2.	만약 참조가 다르다면:
      •	@Stable이면 equals()로 비교
      •	@Immutable이면 값 비교 없이 무조건 새 값으로 간주 → 리컴포지션 발생

```kotlin
val userA = User("Alice")
val userB = User("Alice") // 같은 내용, 다른 객체

@Composable
fun Content(user: User) {
    Text(user.name)
}

// Case 1
Content(userA) // 처음 호출 → 리컴포지션 발생

// Case 2
Content(userA) // 같은 객체 전달 → 리컴포지션 스킵

// Case 3
Content(userB) // 다른 객체 전달 → 항상 리컴포지션 발생
```

정리
•	=== 비교로 객체 동일성 확인:
→ 참조가 같으면 물리적으로 동일한 객체 → 리컴포지션 스킵 가능
•	== 혹은 equals는 보조적인 비교:
→ @Stable 타입일 경우에만 객체 내부 값이 같은 경우 리컴포지션을 건너뛰기 위해 쓰임
•	@Immutable은 equals 호출 없이 무조건 값이 변하지 않는다고 가정 → 내부 값 비교조차 안 함

🟡 @Stable일 때

Compose 입장에서는:
•	“이 클래스는 내가 안정적이라고 약속했구나”
•	즉, 내부 상태에 변화가 없다면 스킵해도 괜찮다
•	즉, equals()가 true면 컴포지션 재사용 👍

⚠️ 하지만 내부 값을 바꾸는 건 개발자가 스스로 관리해야 함 (mutableState 등)

🟢 @Immutable일 때

Compose 입장에서는:
•	“오, 얘는 내부 값도 진짜 변경 불가능하네”
•	내부에 val, 원시 타입(Primitive), 다른 Immutable 타입만 있어야 함
•	이 객체는 절대 변하지 않으니 안전하게 스킵 가능

---
[ ] inline vs noinline
[ ] Stable 과 Immutable 테스트 코드 짜면서 어떤 점이 다른지 비교해보자!!
[ ] SideEffect 공부
[ ] Api Debugger 에 추가