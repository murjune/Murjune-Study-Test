!youtube[3DNbRnl0im4]

테코톡에서는 [9:01 ~ 9:58] 에 해당하는 내용입니다.
지금까지 배운 내용 리마인드 해봅시다.

> - SupervisorJob
> 1) SupervisorJob 은 자식 코루틴의 예외 전파를 방지한다.
> 2) SupervisorJob() 로 생성된 SupervisorJob 은 root Job 이 된다.
> 3) SupervisorJob() 로 생성된 SupervisorJob 은 항상 active 하다.
>
> - 이전 포스팅  
    > [코루틴 예외가 전파되는 방식](https://velog.io/@murjune/kotlin-Coroutine-%EC%BD%94%EB%A3%A8%ED%8B%B4-%EC%98%88%EC%99%B8%EA%B0%80-%EC%A0%84%ED%8C%8C%EB%90%98%EB%8A%94-%EB%B0%A9%EC%8B%9D%EC%9D%84-%EC%95%8C%EC%95%84%EB%B3%B4%EC%9E%90-0lac2p97)  
    > [코루틴 예외 전파 제한 왜 하는거지?(with SupervisorJob)](https://velog.io/@murjune/kotlin-Coroutine-%EC%BD%94%EB%A3%A8%ED%8B%B4-%EC%98%88%EC%99%B8-%EC%A0%84%ED%8C%8C-%EC%A0%9C%ED%95%9C-%EC%99%9C-%ED%95%98%EB%8A%94%EA%B1%B0%EC%A7%80with-SupervisorJob)

## Intro

[지난 포스팅](https://velog.io/@murjune/kotlin-Coroutine-%EC%BD%94%EB%A3%A8%ED%8B%B4-%EC%98%88%EC%99%B8-%EC%A0%84%ED%8C%8C-%EC%A0%9C%ED%95%9C-%EC%99%9C-%ED%95%98%EB%8A%94%EA%B1%B0%EC%A7%80with-SupervisorJob)에서는 이미지를 업로드하는 예시를 통해 `SupervisorJob`로 예외 전파를 제한하는 방법을 배워봤습니다. 사실은 이미지 업로드 예시는 `SupervisorJob` 보다는 `supervisorScope` 을 활용하는 것이 더 적절하다고 했었죠?

코루틴 스코프함수와 `supervisorScope`에 대해 배워본 후, 이미지 업로드 예시를 `supervisorScope` 로 리팩토링해봅시다!💪

## 1. 코루틴 스코프 함수

> 코루틴 스코프 함수에 대해 이미 알고 계신 독자는 이번 챕터는 넘기셔도 좋습니다 😁


코루틴 스코프 함수는 `새로운 코루틴 스코프를 생성하는 suspend 함수` 입니다.
`launch` 나 `async`는 CoroutineScope 확장함수이기에 일반 suspend 함수에서는 호출할 수 없습니다. 

```kotlin
suspend fun foo() = coroutineScope {
    launch { ... } // 호출 불가 ❌
    launch { ... } // 호출 불가 ❌
}
```

이런 경우 coroutineScope 와 같은 코루틴 스코프 함수를 활용합니다.   
(withContext, withTimeOut, supervisorScope.. 등 다양한 스코프 함수가 있습니다.)

```kotlin
suspend fun bar() = coroutineScope {
    launch { ... } // ✅
    launch { ... } // ✅
}
```

좀 더 자세한 설명을 원하시면 [suspend 함수를 Effective 하게 설계하자!](https://velog.io/@murjune/Kotlin-Coroutine-suspend-%ED%95%A8%EC%88%98%EB%A5%BC-Effective-%ED%95%98%EA%B2%8C-%EC%82%AC%EC%9A%A9%ED%95%98%EC%9E%90-fsaiexxw)을 봐주세요 🙏

# 2. 코루틴 스코프 함수의 특징

코루틴 스코프 함수는 다음과 같은 특징을 가지고 있습니다.
> 1) ⭐️ 호출자의 코루틴 컨텍스트를 받아, 호출자 코루틴과 부모-자식관계를 보장한다.
> 2) ⭐️ 호출자 코루틴은 끝날때까지 일시 중단된다. 
> 3) ⭐️ 자식 코루틴의 작업이 끝날때까지 대기한다.
> 4) 일반 함수와 같은 방식으로 예외를 던진다.
> 5) 결과값을 반환한다.

코루틴 빌더 함수(launch) 도 Job을 생성할 때 부모-자식관계를 보장해주는데요, `코루틴 빌더 함수` 와 `코루틴 스코프 함수`를 비교해볼까요?

- 코루틴 빌더 함수 (launch)
```kotlin
fun main() = runBlocking {
    launch { 
        delay(10)
        println("after") 
    }
    println("before") // 먼저 호출
}
```

launch 의 경우에는 비동기적으로 실행되기 때문에 start 하자마자 바로 탈출하기 때문에 `before` 가 먼저 출력됩니다.

<p ailgn="center">
    <img width ="600" src="https://velog.velcdn.com/images/murjune/post/cc297bd9-36ee-45d6-9a40-e12b968d6ddb/image.png" />
  </p>

- 코루틴 스코프 함수 (coroutineScope)
```kotlin
fun main() = runBlocking {
    coroutineScope {
        delay(10)
        println("after") // 먼저 호출
    } 
    println("before")
}
```

coroutineScope 는 suspend 함수이기 때문에 종료할때까지 `호출자 코루틴은 일시중단` 됩니다.
그리고, `after` 가 출력된 이후에 coroutineScope 종료되고 `before` 가 출력됩니다.

<p ailgn="center">
    <img width ="600" src="https://velog.velcdn.com/images/murjune/post/ac013c7b-890e-49d9-bf44-2c3b604f79ec/image.png" />
  </p>

이를 통해 코루틴 빌더 함수(launch)와 달리 코루틴 스코프 함수(coroutineScope)는 호출자 코루틴과 동기적으로 실행된다는 것을 알 수 있습니다😁

## 2. supervisorScope

> 1) ⭐️ 호출자의 코루틴 컨텍스트를 받아, 호출자 코루틴과 부모-자식관계를 보장한다.
> 2) ⭐️ 호출자 코루틴은 끝날때까지 일시 중단된다.
> 3) ⭐️ 자식 코루틴의 작업이 끝날때까지 대기한다.
> 4) 일반 함수와 같은 방식으로 예외를 던진다.
> 5) 결과값을 반환한다.

supervisorScope 함수는 `coroutineScope` 와 같은 코루틴 스코프 함수입니다.
그렇기에 `coroutineScope` 와 같이 위와 같은 특징을 갖는데요, 추가적으로`supervisorScope` 는 `자식 코루틴의 예외 전파를 제한`해줍니다.

![](https://velog.velcdn.com/images/murjune/post/dfa2c4ef-eb66-49be-b190-8f83c1f27be4/image.png)

그 이유는 `supervisorScope` 는 내부적으로 `SupervisorJob` 을 가지고 있기 때문입니다.

간단한 예시로 확인해볼까요?

```kotlin
suspend fun foo() = supervisorScope {
    launch { println("Child 1") }
    launch { error("예외 발생 😵") }
    launch { println("Child 2") }
    launch { println("Child 3") }
}

suspend fun main() {
    println("시작")
    foo()
    println("끝")
}
```

<p ailgn="center">
    <img width ="600" src="https://velog.velcdn.com/images/murjune/post/8da61ac0-feb4-4387-b6c1-c36d6056a7a5/image.png" />
  </p>

child 코루틴에서 예외가 발생해도 부모 코루틴에 예외를 전파시키지 않고 있네요!

> 위 예제의 `supervisorScope` 를 `coroutineScope` 로 바꿔서 실행한 결과와 비교해보시길 추천드려요

추가적으로 `supervisorScope`을 처음 사용하면 자주 오인하는 부분이 있습니다.

#### supervisorScope {} 내부에서 발생하는 예외는 전파합니다. 자식 코루틴의 예외를 전파 제한합니다.

```kotlin
suspend fun foo() = supervisorScope {
    error("예외 전파 제한 못함 🤯")  
    launch { println("Child 1") }
    launch { println("Child 2") }
}
```

![](https://velog.velcdn.com/images/murjune/post/e9f3dc9e-677d-4e3d-83d0-abb7a4aeea44/image.jpg)

헷갈릴 수 있는 부분이기에 주의해주세요 😎


## 3. 이미지 업로드 예제 supervisorScope 로 리팩토링

```kotlin
suspend fun uploadImages(localImagePaths: List<String>): List<String?> = coroutineScope {
    val supervisor = SupervisorJob(coroutineContext.job) // 부모 코루틴 설정
    val result = localImagePaths.map { localImagePath ->
        async(supervisor) { uploadImage(localImagePath) } { uploadImage(localImagePath) }
    }.map {
        try { // await() 예외 처리
            it.await()
        } catch (e: IllegalStateException) {
            null
        }
    }
    supervisor.complete() // supervisor 명시적 종료
    result
}
```
[코루틴 예외 전파 제한 왜 하는거지?](https://velog.io/@murjune/kotlin-Coroutine-%EC%BD%94%EB%A3%A8%ED%8B%B4-%EC%98%88%EC%99%B8-%EC%A0%84%ED%8C%8C-%EC%A0%9C%ED%95%9C-%EC%99%9C-%ED%95%98%EB%8A%94%EA%B1%B0%EC%A7%80with-SupervisorJob) 에서 사용한 다수의 이미지 업로드하는 예제에요.

SupervisorJob() 으로 생성된 Job 의 특징 때문에 아래와 같은 작업을 처리해줬습니다.

```kotlin
 // 1. 부모-자식 관계 설정
 val supervisorJob = SupervisorJob(coroutineContext.job)
 CoroutineScope(supervisorJob).launch { .. }
 // 2. supervisor 명시적 종료
 supervisorJob.complete()
```
딱 봐도 복잡해보이죠? 😤  
이제 supervisorScope 를 활용해서 이미지 업로드 예제를 리팩토링해봅시다.

```kotlin
suspend fun uploadImages(localImagePaths: List<String>): List<String?> = supervisorScope {
    localImagePaths.map { localImagePath ->
        async { uploadImage(localImagePath) } { uploadImage(localImagePath) }
    }.map {
        try {
            it.await()
        } catch (e: IllegalStateException) {
            null
        }
    }
}
```

훨씬 깔끔해졌죠? (try-catch 문 때문에 그렇게 안보일 수도 있겠지만 😅)
이렇듯, 독립적인 작업들을 병렬 처리해야할 경우에 supervisorScope 를 많이 사용합니다.

> supervisorScope 을 사용한 예시를 좀 더 보고 싶으시면 [suspend 함수를 Effective 하게 설계하자!](https://velog.io/@murjune/Kotlin-Coroutine-suspend-%ED%95%A8%EC%88%98%EB%A5%BC-Effective-%ED%95%98%EA%B2%8C-%EC%82%AC%EC%9A%A9%ED%95%98%EC%9E%90-fsaiexxw) 의 마지막 챕터를 봐주세요~

## 4. 정리

> - supervisorScope
> 1) 호출자의 코루틴 컨텍스트를 받아, 호출자 코루틴과 부모-자식관계를 보장한다.
> 2) 호출자 코루틴은 끝날때까지 일시 중단된다.
> 3) 자식 코루틴의 작업이 끝날때까지 대기한다.

오늘은 코루틴 스코프 함수에 대해 간략하게 알아보고 그 중 `supervisorScope` 를 활용해 예외를 전파하는 방법에 대해 알아 봤어요.

`SupervisorJob`, `supervisorScope` 둘다 배워보니 `supervisorScope`가 훨씬 사용하기 쉽고 편하다는 생각이 들거에요. 그럼 예외를 전파할때는 무조건 supervisorScope 만을 사용하면 될까요?   🤔

다음 포스팅에서는 `supervisorJob`과 `supervisorScope`의 차이점을 비교하고, 각각을 적절하게 사용하는 방법과 실제 사용 사례를 소개해드리겠습니다. 👋


