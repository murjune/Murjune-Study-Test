!youtube[3DNbRnl0im4]

안녕하세요! 테코톡 `코루틴 예외` 발표한 우아한테크코스 6기 `오둥이`입니다.  
10 분이라는 짧은 시간적인 제약 때문에 전달하고 싶은 지식을 최대한 축약해서 전달할 수 밖에 없었기에  
코루틴 예외에 대한 보충 설명 및 상세한 설명을 포스팅하려 합니다.

이번 포스팅에서는 `SupervisorJob 을 활용해서 예외 전파 제한하는 방법`에 대해서 알아볼 것입니다.  
테코톡에서는 [3:48 ~ 8:12] 에 해당하는 내용입니다.

지난 시간에 배운 내용 리마인드~
> - 코루틴 예외 전파 메커니즘
> 1) 예외가 발생할 시, `자기 자신`을 취소시킨다. (자식 코루틴들 모두 취소)
> 2) 예외 발생 시, `부모로 예외를  전파`시킨다. (부모, 형제 코루틴들 모두 취소)
>
> 좀 더 궁금하신 분은 이전에 포스팅한 [코루틴 예외가 전파되는 방식](https://velog.io/@murjune/kotlin-Coroutine-%EC%BD%94%EB%A3%A8%ED%8B%B4-%EC%98%88%EC%99%B8%EA%B0%80-%EC%A0%84%ED%8C%8C%EB%90%98%EB%8A%94-%EB%B0%A9%EC%8B%9D%EC%9D%84-%EC%95%8C%EC%95%84%EB%B3%B4%EC%9E%90-0lac2p97)을 참고해주세요



---

# 예외 전파 제한이 필요한 경우
코루틴을 활용하여 비동기 작업을 하다 보면 하나의 작업을 여러 작업으로 쪼개 병렬처리하는 경우가 종종 있습니다. 보통 suspend 함수에서 코루틴 빌더함수 [async](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/async.html) 와 코루틴 스코프 함수[coroutineScope](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/coroutine-scope.html)를 활용하여 처리합니다.

> async 와 coroutineScope 를 사용하여 병렬 처리하는 이유를 자세히 알고 싶으신 분은 [Kotlin Coroutine: suspend 함수를 Effective 하게 설계하자!](https://velog.io/@murjune/Kotlin-Coroutine-suspend-%ED%95%A8%EC%88%98%EB%A5%BC-Effective-%ED%95%98%EA%B2%8C-%EC%82%AC%EC%9A%A9%ED%95%98%EC%9E%90-fsaiexxw#3-coroutinescope-or-withcontext-%ED%95%A8%EC%88%98%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%98%EC%9E%90) 에서 `2) suspend function 에서 병렬 처리할 때, CoroutineScope를 사용하지 말자` 와 `3) coroutineScope or withContext 함수를 활용하자!⭐️` 부분을 참고해주세요 😉

로컬 저장소의 이미지 경로를 통해 서버에 이미지들을 업로드한 후, 이미지 url을 받아오는 예제를 통해  `예외 전파 제한이 필요성`에 대해 알아볼 것이에요!😎

<p ailgn="center">
    <img width ="500" src="https://velog.velcdn.com/images/murjune/post/726ad566-a5b1-46fc-8eff-753bc07c0091/image.png" />
  </p>

현재 다수의 이미지를 업로드하고 있습니다. 하나의 이미지 업로드 당 하나의 child 코루틴에게 할당하여 병렬처리하였습니다. 코드로 보면 다음과 같습니다.

```kotlin
suspend fun uploadImages(localImagePaths: List<String>): List<String> = coroutineScope {
    localImagePaths.map { localImagePath ->
        async { uploadImage(localImagePath) }
    }.awaitAll()
}

fun main() = runBlocking {
    val paths = listOf("이미지 1", "이미지 2", "이미지 3", "이미지 4")
    val result = uploadImages(paths)
    println(result)
    // output: ["서버 이미지 1", "서버 이미지 2", "서버 이미지 3", "서버 이미지4"]
} 
```

언듯 보기에는 별 문제가 없는 코드입니다.

#### 이때! '이미지 4' 에 해당하는 이미지를 업로드할 때 에러가 발생했다고 해봅시다.

<p ailgn="center">
    <img  src="https://velog.velcdn.com/images/murjune/post/5921a79d-bf0d-47e6-8c63-9c76a53b583e/image.png" />
  </p>

그럼, '이미지 4' 에서 발생한 이미지는 coroutineScope 코루틴에게 예외를 전파하고 모든 이미지 업로드 작업들을 취소시킬 것입니다.

<p ailgn="center">
    <img width ="500" src="https://velog.velcdn.com/images/murjune/post/c24729ca-ee3c-48c1-a0a1-b918e08094c2/image.png" />
  </p>


그러면 사용자는 다음과 같은 화면을 마주하게 될 것입니다.

<p ailgn="center">
    <img width ="500" src="https://velog.velcdn.com/images/murjune/post/8a4012d2-33e3-42f3-885b-0d0d6567e7ee/image.png" />
  </p>

현재, 기획단에서는 업로드에 실패한 이미지만 에러뷰를 보여주고, 업로드에 성공한 이미지는 모두 보여달라고 요청하고 있습니다. 어떻게 해야할까요?

바로 이럴 때 `SuperVisorJob` 을 활용하여 `예외 전파 제한`을 활용하여 해결할 수 있습니다.  
[SuperVisorJob](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-supervisor-job.html) 에 대해 알아봅시다!

# SupervisorJob

SupervisorJob 은 `자식 코루틴으로부터 예외를 전파받지 않은` 특수한 Job 이고, SupervisorJob() 팩토리 함수를 통해 생성할 수 있습니다.

![](https://velog.velcdn.com/images/murjune/post/44548afb-2b5f-490d-8ee0-a765cab491b7/image.png)

SupervisorJob() 에 의해 생성된 SupervisorJob 은 [Job()](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job.html) 팩토리 함수와 `자식 코루틴의 예외 전파를 방지 제한`한다는 점을 빼고 동일합니다. 팩토리 함수에 의해 생성된 Job 은 같이 다음 2가지 특징을 가지고 있습니다.

> 1) 부모 코루틴과의 구조화된 동시성을 깬다
> 2) Job 팩토리 함수를 통해 생성된 Job 은 항상 active 하다

위 특징과 `부모 코루틴은 자식 코루틴이 작업을 끝날 때까지 기다린다` 는 코루틴의 특징을 함께 생각해보면
SupervisorJob() 을 왜 유의해서 사용해야하는지 알 수 있습니다.

한 번 곰곰히 생각해보고 다음 챕터를 읽어보시죠 🤔

## SupervisorJob() 의 유의점 1 : 독립적인 코루틴이 될 수 있다

Job() 과 동일하게 SuperVisorJob() 으로 생성된 Job 은 파라미터로 부모 Job 을 넣어주지 않으면 새로운 `root Job`이 됩니다. 즉, SuperVisorJob() 을 호출한 코루틴과의 부모-자식 관계가 끊어진다는 점을 뜻합니다.


<p ailgn="center">
    <img width ="400" src="https://velog.velcdn.com/images/murjune/post/1c89b72d-9fc6-4590-804c-b1e12a5a0e81/image.png" />
  </p>

부모 자식 관계가 깨지게되면 `호출자 코루틴`은 더이상 `SupervisorJob`을 기다리지 않게 됩니다.

```kotlin
suspend fun foo() = coroutineScope {
    val job = SupervisorJob()
    launch(CoroutineName("Child") + job) { // coroutineScope 코루틴과 독립적인 코루틴
        delay(10)
        println("나를 이제 기다리지마오~") // 출력 ❌
    }
    println("끝")
}
```

coroutineScope 는 SupervisorJob() 와 독립적인 코루틴 관계가 되기에 Child 코루틴이 끝날 때까지 대기해주지 않습니다.

<p ailgn="center">
    <img width ="500" src="https://velog.velcdn.com/images/murjune/post/c4254076-0a28-460a-a1f4-b18daaa31e9d/image.png" />
  </p>

따라서, 부모-자식 관계를 깨고 싶지 않다면 SupervisorJob() 의 부모를 coroutineScope 의 job 로 설정해주어야합니다.

```kotlin
suspend fun foo() = coroutineScope {
    val supervisorJob = SupervisorJob(parent = coroutineContext.job)
    launch(CoroutineName("Child") + supervisorJob) {
        ...
        println("이제 출력됨 ✅")
    }
    println("끝")
}
```
<p ailgn="center">
    <img width ="400" src="https://velog.velcdn.com/images/murjune/post/02ff5910-4d1a-4be5-acd5-301dda397e05/image.jpg" />
  </p>

현재 foo() 은 종료가 되지 않고 있습니다.  
왜 그럴까요? 그건 Job 이 active 한 상태이기 때문입니다.

## SuperVisorJob() 의 유의점 2 : 항상 active 하다

![](https://velog.velcdn.com/images/murjune/post/697e81f9-d752-477b-961d-44a6b2623834/image.png)

일반적인 Job 빌더함수 `launch(), async()` 를 통해 생성된 Job은 위와 같은 생명주기를 갖습니다. launch 블럭이 끝나면 `Completed` 상태, 취소가 되면 `Canceled` 상태로 종료됩니다.

그러나, `SuperVisorJob(), Job()` 와 같은 잡 팩토리 함수에 의해 생성된 Job 은 항상 `active` 합니다. 따라서, coroutineScope 입장에서는 supervisorJob 이 계속 active 하기에 끝날때까지  
계속 대기하는거죠

<p ailgn="center">
    <img width ="350" src="https://velog.velcdn.com/images/murjune/post/dba02446-af6b-4cfd-9207-33478cd3e8cf/image.png" />
  </p>

따라서, complete() 함수를 통해 명시적으로 job 을 종료시켜주어야합니다.

> complete() : 잡의 상태를 completed 상태로 만듦. 만약, 자식 코루틴이 아직 active 하다면 완료될 때까지 기다린 후 completed 상태가 됨

```kotlin
suspend fun foo() = coroutineScope {
    val supervisorJob = SupervisorJob(parent = coroutineContext.job)
    launch(CoroutineName("Child") + supervisorJob) {
        ..
    }
    supervisorJob.complete() // 명시적으로 종료
    println("끝")
}
```
이제야 작업을 마치고 프로그램을 종료하네요 😁

<p ailgn="center">
    <img width ="500" src="https://velog.velcdn.com/images/murjune/post/d1f3f14e-65f9-43da-85f4-2cc5fe2cd650/image.png" />
  </p>

자 그럼 이제 이미지 업로드하는 예시에 SupervisorJob() 을 적용해볼까요?

# 이미지 업로드 예제: SupervisorJob() 적용

이미지 업로드 예제에서 한가지 더 처리해줘야합니다. 바로 `await() 를 할 때 예외 처리를 해줘야합니다.` `async{}`는 Deferred 잡 객체에 결과값을 저장하고, await() 를 통해 결과값을 불러오는 특징이 있습니다. 그래서, async{} 블럭 내부에 예외를 발생시킬 경우, await() 를 호출하면 예외가 발생합니다.

#### 따라서, await() 를 호출하는 부분에 try-catch 로 감싸주어 예외처리 해주어야합니다.

완성된 코드는 다음과 같습니다!

```kotlin
suspend fun uploadImage(imagePath: String): String = withContext(Dispatchers.IO) {
    delay(100) // 로컬 이미지를 불러와 Form 데이터 형태로 바꾸는 작업이라 가정
    if (imagePath == "이미지 4") error("예외 발생 😵")
    val imageUrl = "서버 이미지: $imagePath"
    imageUrl
}


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

fun main() = runBlocking {
    val localImagePaths = listOf("이미지 1", "이미지 2", "이미지 3", "이미지 4")
    val images = uploadImages(localImagePaths)
    println(images)
}
```

<p ailgn="center">
    <img width ="500" src="https://velog.velcdn.com/images/murjune/post/ea1646f8-a90b-4ac3-be56-7f3e790e7b72/image.png" />
  </p>


예외가 발생한 이미지의 경우에는 null 을 반환하도록 했습니다.
그럼 사용자는 기획이 원하는 화면을 마주할 수 있겠습니다 😁

<p ailgn="center">
    <img width ="500" src="https://velog.velcdn.com/images/murjune/post/3d96b53d-9cda-4b13-a29d-ada76b62b6b5/image.png" />
  </p>

위 코드 구조를 그림으로 나타내면 다음과 같습니다.

<p ailgn="center">
    <img width ="500" src="https://velog.velcdn.com/images/murjune/post/edcb9fc3-0b43-4597-a57b-d928d2aefd77/image.png" />
  </p>


# SuperVisorJob() 을 사용할 때 자주하는 실수

SupervisorJob 을 처음 사용할 때 자주하는 실수입니다.

CoroutineScope 안에 supervisorJob 을 넣거나, launch 에 supervisorJob을 넣고 그 내부 블럭에
launch{} 를 열면 예외 전파 방지가 되지 않습니다.

코드로 보면 다음과 같습니다.

```kotlin
suspend fun foo() = coroutineScope {
    val supervisor = SupervisorJob(coroutineContext.job)
    // 잘못된 예외 전파 방식 1
    CoroutineScope(supervisor).launch {
        launch { error("에러") }
    }
    // 잘못된 예외 전파 방식 2
    launch(supervisor) { 
        launch { error("에러") } 
    }
}
```

그 이유는 SupervisorJob 과 coroutineScope 사이에 launch 빌더에 의해 만들어진 Job 이 존재하기 때문입니다.
아리까리 하면 다음 그림으로 보시면 이해하기 쉬울거에요!

<p ailgn="center">
    <img width ="500" src="https://velog.velcdn.com/images/murjune/post/cbb161e1-e133-45d7-9779-4c47aa81e0b5/image.png" />
  </p>


> 추가로 제가 테코톡에서 7분 32초 경 아래 슬라이드에서 해당 부분을 잘못 설명했습니다 ^__^

<p ailgn="center">
    <img width ="500" src="https://velog.velcdn.com/images/murjune/post/a4e2ae04-71b9-483d-a37b-84f94c02f501/image.jpg" />
  </p>

# 정리

> 1) SupervisorJob 은 자식 코루틴의 예외 전파를 방지한다.
> 2) SupervisorJob() 로 생성된 SupervisorJob 은 root Job 이 된다.
> 3) SupervisorJob() 로 생성된 SupervisorJob 은 항상 active 하다.

오늘은 이미지를 업로드하는 예시를 통해 `SupervisorJob`로 예외 전파를 제한하는 방법을 배웠습니다.

사실 오늘 예시에서는 위 2번, 3번 특징 때문에 `supervisorScope`를 사용하는 것이 더 적절한데요! 다음 포스팅에서는 위 이미지 업로드 예시를 `supervisorScope` 로 리팩토링해보면서 왜 `supervisorScope` 가 더 적절한지 배울 것입니다 💪


> 이번 포스팅에서 사용된 예시는 supervisorScope 를 설명하기 위한 빌드업으로 사용된 것이니 위 예시> 를 실 프로젝트 코드에 적용하는 것은 비추천드립니다 😨
> `SupervisorJob` 은 CoroutineScope() 와 함께 `root Coroutine` 에서 사용하는 것이 더 적절한데요 이 내용도 추가로 포스팅하도록 하겠습니다 
