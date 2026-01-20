
클로드도 [할루시네이션 리뷰] 때문에 공부한게 되었다.

아래와 같은 `isActive`를 활용한 분기처리는 불필요

```kotlin
suspendCancellableCoroutine { continuation ->
    linkGenerator.generateLink(context, object : LinkGenerator.ResponseListener {
        override fun onResponse(oneLink: String?) {
            if (!continuation.isActive) return  // 👈 불필요
            
            continuation.resume(oneLink)
        }
    })
}
```

continuation 이 `Cancelled `상태일 때 `resume()`가 호출되면 `CancellationException`이 발생하게 되어 불필요한 분가처리는 할 
필요 없음.


- 공식 문서 : [kotlinx-coroutines-core/common/src/CancellableContinuation.kt ](https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/common/src/CancellableContinuation.kt)


# CancellableContinuation 공식 문서 쉬운 번역

## 개요

`CancellableContinuation`은  
**취소(cancellation)를 지원하는 continuation**이며, **멀티 스레드 환경에서도 안전하게 동작**하도록 설계되어 있습니다.

일반 `Continuation`은 resume만 가능하지만,  
`CancellableContinuation`은 다음도 가능합니다.

- 직접 cancel 가능
- 부모 Job이 cancel 되면 자동 cancel
- cancel 되면 `CancellationException`(또는 지정한 원인)으로 종료됨

---

## 사용 방법

`CancellableContinuation`은 오직  
`suspendCancellableCoroutine` 내부에서만 얻을 수 있습니다.

주된 목적은 다음과 같습니다.

> 콜백 기반 API를 suspend 함수로 감쌀 때  
> 그리고 그 콜백이 **취소도 지원해야 할 때**

즉,

- 외부 SDK 콜백
- 네트워크 SDK
- 딥링크 SDK
- 광고 SDK

같은 곳에서 쓰기 위해 존재하는 타입입니다.

---

## Thread-safety (스레드 안전성)

`CancellableContinuation`은 여러 스레드에서 동시에 접근해도 안전합니다.

보장되는 규칙은 다음과 같습니다.

- `cancel()` 과 `resume()` 이 동시에 호출되어도  
  → **둘 중 하나만 성공**
- `resume()` 을 두 번 호출하면  
  → **프로그래머 실수 → IllegalStateException 발생**
- `cancel()` 은 여러 번 호출돼도 괜찮음  
  → **최초 1회만 성공 처리**

---

## Prompt Cancellation Guarantee (가장 중요한 개념)

이게 이 타입의 핵심 가치입니다.

> 코루틴이 suspend 상태일 때 Job이 취소되면,  
> resume이 이미 호출됐더라도  
> **해당 코루틴은 정상적으로 재개되지 않는다.**

왜 이런 보장이 필요할까요?

코루틴은 resume 되더라도 즉시 실행되는 것이 아니라,  
Dispatcher 큐에 들어가서 "나중에 실행"됩니다.

즉, 이런 상황이 가능합니다.

1. 콜백에서 resume 호출
2. 아직 실행되기 전
3. 그 사이 Job.cancel() 호출됨

이 경우:  
→ 코루틴은 **resume 결과를 무시하고 CancellationException으로 종료됨**

이 덕분에:

- 이미 취소된 작업이 실행되는 문제
- 취소된 작업이 리소스를 계속 사용하는 문제  
  를 원천적으로 막을 수 있습니다.

---

## resume(exception) vs cancel 경쟁 시 동작

만약 다음 두 이벤트가 동시에 발생하면:

- resumeWithException 호출
- cancel 호출

어느 쪽이 먼저 발생했는지에 따라 결과 예외가 결정됩니다.  
즉, **경쟁 상태에서도 예측 가능한 동작**을 보장합니다.

---

## 리소스를 안전하게 다루기 위한 설계

문서에서 매우 중요하게 강조하는 부분입니다.

코루틴이 resume 되었더라도,  
dispatcher에서 실행되기 전에 취소될 수 있기 때문에  
**resume 값으로 전달한 리소스가 유실될 위험**이 있습니다.

그래서 `CancellableContinuation`은 다음을 보장합니다.

> resume(value, onCancellation)을 사용하면
> - value가 정상적으로 전달되거나
> - 그렇지 않으면 onCancellation이 반드시 호출된다

즉,

- 리소스를 반환하는 suspend 함수
- 파일, 소켓, 핸들, SDK 객체

같은 경우에도 **리소스 누수 없이 안전하게 처리 가능**

---

## 상태(State)

CancellableContinuation은 다음 3가지 상태만 가집니다.

| 상태      | 의미 |
|-----------|------|
| Active    | 아직 resume / cancel 되지 않은 상태 |
| Resumed   | 정상적으로 값 또는 예외로 종료 |
| Cancelled | 취소되어 종료 |

전이 규칙:

- Active → Resumed : resume 호출
- Active → Cancelled : cancel 호출

중간 상태나 애매한 상태는 존재하지 않도록 설계되어 있습니다.

---

## 공식 문서 핵심 한 줄

> CancellableContinuation은  
> **"취소와 resume 경쟁 상황에서도 안전성과 리소스 정리를 보장하기 위한 continuation"** 이다.
