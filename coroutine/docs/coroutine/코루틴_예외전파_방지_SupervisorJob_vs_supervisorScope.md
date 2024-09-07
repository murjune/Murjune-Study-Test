
!youtube[3DNbRnl0im4]

테코톡에서는 [8:13 ~ 9: 00] 에 해당하는 내용입니다.

지난 시간에 배운 내용 정리~
> - SupervisorJob
> 1) SupervisorJob 은 자식 코루틴의 예외 전파를 방지한다.
> 2) SupervisorJob() 로 생성된 SupervisorJob 은 root Job 이 된다.
> 3) SupervisorJob() 로 생성된 SupervisorJob 은 항상 active 하다.
>
> - supervisorScope
> 1) `자식 코루틴의 예외 전파를 방지`한다.
> 2) 호출자의 코루틴 컨텍스트를 받아, 호출자 코루틴과 부모-자식관계를 보장한다.
> 3) 호출자 코루틴은 끝날때까지 일시 중단된다.
> 4) 자식 코루틴의 작업이 끝날때까지 대기한다.

> - 이전 포스팅  
    > [1. 코루틴 예외가 전파되는 방식](https://velog.io/@murjune/kotlin-Coroutine-%EC%BD%94%EB%A3%A8%ED%8B%B4-%EC%98%88%EC%99%B8%EA%B0%80-%EC%A0%84%ED%8C%8C%EB%90%98%EB%8A%94-%EB%B0%A9%EC%8B%9D%EC%9D%84-%EC%95%8C%EC%95%84%EB%B3%B4%EC%9E%90-0lac2p97)  
    > [2. 코루틴 예외 전파 제한 왜 하는거지?(with SupervisorJob)](https://velog.io/@murjune/kotlin-Coroutine-%EC%BD%94%EB%A3%A8%ED%8B%B4-%EC%98%88%EC%99%B8-%EC%A0%84%ED%8C%8C-%EC%A0%9C%ED%95%9C-%EC%99%9C-%ED%95%98%EB%8A%94%EA%B1%B0%EC%A7%80with-SupervisorJob)  
    > [3. 코루틴 예외 전파 제한 (supervisorScope)](https://velog.io/@murjune/kotlin-Coroutine-%EC%BD%94%EB%A3%A8%ED%8B%B4-%EC%98%88%EC%99%B8-%EC%A0%84%ED%8C%8C-%EC%A0%9C%ED%95%9C-supervisorScope)

# Intro

지난 포스팅에서 `SupervisorJob()` 과 `supervisorScope` 에 대해 알아보았습니다.
`supervisorScope` 이 자동으로 호출자 코루틴과의 부모-자식 관계를 보장해주기에 사용하기 훨씬 편합니다.

> `supervisorScope` 사용하기 편하다는 거 알겠어 그럼 `SupervisorJob()`는 언제 씀? 🤔

그럼 위와 같은 생각이 들 수도 있는데요, `supervisorJob`과 `supervisorScope`의 차이점을 비교하고, 각각을 적절하게 사용하는 방법과 실제 사용 사례를 소개해드리겠습니다. 👋

# 1. 일반적인 경우에는 supervisorScope 를 사용하자

SupervisorJob 과 supervisorScope 를 사용해 예외 전파를 방지하는 예제입니다.

```kotlin
SupervisorJob 예시

supervisorScope 예시 
```

`SupervisorJob()` 을 사용하면 구조화가 무너지기에 추가적인 설정들이 필요합니다.  
확실히 `supervisorScope` 를 사용하는 것이 편해보이네요

그래서, 코루틴 내부(코루틴 스코프 내부)나 suspend 함수에서는 `supervisorScope` 를 활용하여 예외 전파 방지하는 것이 훨씬 낫습니다.

# 2. CoroutineScope 를 생성할 때 SupervisorJob 을 사용하자

supervisorScope 는 suspend 함수이기에, 일반함수에서는 사용할 수 없다는 제약이 있습니다.

> 참고) suspend 함수는 같은 suspend 함수나 코루틴 내부(코루틴 스코프 내부)에서만 호출할 수 있습니다.
>
> ![](https://velog.velcdn.com/images/murjune/post/9a9800dc-8405-430c-a21a-f3b734c57f40/image.png)

그래서, 일반 함수에서 새롭게 코루틴을 생성하고 사용할 때는 `CoroutineScope` 의 coroutineContext 에 `SupervisorJob()` 을 지정해줘야합니다.

CoroutineScope 에 SupervisorJob() 을 지정안해주면 어떤 문제가 있을까요?


```kotlin
val scope = CoroutineScope(CoroutineExceptionHandler { _, _ -> println("예외 발생") })

fun loadImages() = scope.launch { println("이미지..") }

fun loadUsers() = scope.launch { error("error 😵") }

fun loadCustomers() = scope.launch { println("손님..") }
```

이미지, 유저, 손님 정보를 비동기적으로 동시에 불러오고 있습니다. 그리고, 다음 시간에 배울 CoroutineExceptionHandler 를 통해 예외 처리도 해주고 있습니다.

````kotlin
loadImages()
loadUsers()
loadCustomers()
````
실행하면 어떻게 될까요?

![](https://velog.velcdn.com/images/murjune/post/af0ba774-2bc0-4b2a-bf6c-1f6655084f40/image.png)

이미지와 손님 정보를 불러오는 코루틴들이 모두 취소가 되었습니다 😨  
이는 `loadUsers()` 에서 발생한 예외가 CoroutineScope 내부에 있는 coroutineContext 의 Job 에 전파되어 CoroutineScope 가 관리하는 모든 코루틴이 취소되었기 때문입니다.

![](https://velog.velcdn.com/images/murjune/post/b4d8aa41-52ed-401d-8cbf-34c6e235da0b/image.png)

> 참고로 CoroutineExceptionHandler 는 예외만 처리하는 것이지 예외 전파는 막지 못합니다.
> 그래서, 모두 취소 된 것

CoroutineScope 가 담당하는 코루틴들 중 하나의 코루틴에서 예외가 발생했다고 모든 코루틴이 취소 되는 것은 아무래도 이상합니다.😨  그래서, 이런 경우에 CoroutineScope() 의 인자에 `SupervisorJob()` 을 넣어주어 `예외 전파 제한`해주어야 합니다.

```kotlin
val scope = CoroutineScope(SupervisorJob() + CoroutineExceptionHandler { _, _ -> println("예외 발생") })
```

이제는 loadUsers() 에서 예외가 발생해도 다른 코루틴에 영향을 주지 않습니다 😁

![](https://velog.velcdn.com/images/murjune/post/8f1f03cb-85ab-4c46-a78f-f6061144f88b/image.png)

이렇듯 `CoroutineScope` 를 생성할 때, Root 코루틴 컨택스트에 SupervisorJob 을 설정해두어 예외 전파 방지하는 것이 좋습니다.

그럼 이제 안드로이드에서는 SupervisorJob() 을 어떻게 사용하는지 볼까요??

# 3. lifecycleScope, viewModelScope

- lifecycleScope
  ![](https://velog.velcdn.com/images/murjune/post/c571c6de-aa79-4fb1-b0d9-d78eb3609a62/image.jpg)
- viewModelScope
  ![](https://velog.velcdn.com/images/murjune/post/cf74837f-8f34-4711-b0e0-47cae285d186/image.png)

안드로이드에서는 `viewModelScope` 와 `lifecycleScope` 를 생성할 때 coroutineContext 에 `SupervisorJob()` 를 설정해줍니다. 그래서 지금까지 ViewModel 작업할 때 하나의 코루틴에서 예외가 발생해도 다른 작업들이 취소되지 않았던 것이에요 🤭

이번에는 제가 프로젝트에서 로깅 분석을 비동기 처리하기 위해 CoroutineScope 를 만들 때 SupervisorJob() 을 직접 사용한 사례를 소개해드리겠습니다~


# 4. loggerScope, analyticsScope

안드로이드에서는 사용자의 행동 분석, 에러 모니터링을 위해 Firebase Analytics, Crashlytics 를 사용하곤 합니다.  
어떤 작업에 로그를 남기거나 분석하는 작업이 실 서비스의 성능에 영향을 주면 안될 것입니다.
따라서, 로깅 작업 같은 경우 비동기 처리하는 것이 적절할 것입니다.

만약, 특정 id 에 해당하는 유저 정보를 받아오는 Repository 가 있다고 해봅시다.
```kotlin
suspend fun userDetail(id: String): User = coroutineScope {
     userDataSource.userDetail(id).also {
         launch { analytics.logUserEvent(id) }
     }
 }
```
조회한 User id를 analytics 에 로그를 남기는 작업을 비동기 처리하였습니다.
이때, 2가지 문제점이 있습니다.
### 1)  성능 저하

비동기 처리를 하기 위해 coroutineScope 와 launch 를 사용했지만, coroutineScope 은 모든 자식이 끝날때까지 대기합니다. 따라서,launch 내부 로그 분석 작업이 끝나야 `userDetail()`가 종료됩니다.
해당 코드는 비동기 처리 작업을 하느니만 못한 잘못된 코드입니다..😨

### 2) 예외 전파
```kotlin
launch { analytics.logUserEvent(id) }
```
만약 `analytics.logUserEvent(id)` 에서 예외가 발생하면 coroutineScope 로 예외가 전파됩니다.
user 정보를 불러오는데 성공했는데 로그 분석에 실패했다고 실 서비스 코드가 실패하는 것은 절대 안될 일입니다 😨

그래서 저는 다음과 같이 `analyticsScope`라는 새로운 코루틴 스코프를 만들었습니다.
```kotlin
/**
 *  비동기적으로 데이터 수집, 분석, 로깅 등의 작업을 처리하는 용도로 사용하는 CoroutineScope
 */
private val analyticsExcpetionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
    Timber.e(throwable)
}
val analyticsScope = CoroutineScope(SupervisorJob() + Dispatchers.IO + analyticsExcpetionHandler)
```
그리고 `analyticsScope`를 활용해 다음과 같이 수정하였습니다.
```kotlin
suspend fun userDetail(id: String): User = coroutineScope {
     userDataSource.userDetail(id).also {
         analyticsScope.launch { analytics.logUserEvent(id) }
     }
 }
```
이로써 실 서비스에 영향을 주지도 않고, 안전하고 효율적으로 모니터링을 할 수 있게 되었습니다 😎

> 🚨 일반적으로, 코루틴의 구조화를 깨는 것은 비동기 작업을 안전하게 처리할 수 없도록 하기에 최대한 지양 해야합니다. 해당 코드는 `로깅&모니터링` 이라는 특수한 경우이기에 구조화를 깨고 독립적인 작업으로 실행한 것입니다

# 정리

> - `SupervisorJob`
    > CoroutineScope 를 생성할 때 coroutineContext에 SupervisorJob 을  지정해주자.
> - `supervisorScope`
    > 그 외, 예외 전파 제한할 경우 사용하자. (CoroutineScope {} 내부 or suspend 함수)

그럼 다음 포스팅 때는 코루틴 예외 처리하는 방법(CoroutineExceptionHandler)에 대해 소개해드리겠습니다~