> - 해당 포스팅에서 언급하는 코루틴은 [kotlin-coroutine](https://kotlinlang.org/docs/coroutines-overview.html) 입니다.
> - 코루틴에 대한 기본 지식이 없다면 해당 포스팅을 이해하기 어려울 수 있습니다.
> - 내부적으로 suspend function 이 어떻게 중단/재개되는지 다루지 않습니다.
> - 이번 포스팅과 [suspend function 공식문서](https://kotlinlang.org/docs/composing-suspending-functions.html#structured-concurrency-with-async) 을 함께 읽는 것을 추천해요 ⭐️

## Intro
간단한 예제를 통해 기본적인 suspend function 활용법에 대해 알아보자

```kotlin
// super 개발자의 삶..🫢
fun main() = runBlocking {
    println("아침에 일어난다")
    println("밥을 먹는다.")
    delay(100)
    println("코딩하기")
    println("밥을 먹는다.")
    delay(100)
    println("코딩하기")
    delay(100)
    println("밥을 먹는다.")
    delay(100)
    println("코딩하기")
    delay(100)
    println("잠을 잔다.")
}
```
현재 "코딩하기"와 "밥을 먹는다." 코드가 중복해서 사용되고 있다. 이런 경우, 함수화를 통해 코드구조를 개선시키고 싶을 것이다.

일반 함수의 경우 일시 중단을 지원하지 않기 때문에 코루틴을 일시 중단하는 `delay()` 를 호출 할 수 없다.(참고로 delay() 함수도 suspned 함수이다.)


<p ailgn="center">
<img src="https://velog.velcdn.com/images/murjune/post/f62a7415-1e4b-4954-81ae-1e247acf62d9/image.png" width=300 heigh=400>
  </p>


이럴 때, `일시 중단가능한` suspend function 을 사용해 함수화를 해주면 된다.

<p ailgn="center">
<img src="https://velog.velcdn.com/images/murjune/post/4ea0a23d-2a14-49ca-9b4f-52b0080b2b9f/image.png" width=300 heigh=400>
  </p>


```kotlin
suspend fun coding() {
    println("코딩하기")
    delay(100)
}

suspend fun eat() {
    println("밥을 먹는다.")
    delay(100)
}

fun main() = runBlocking {
    println("아침에 일어난다")
    eat()
    coding()
    eat()
    coding()
    eat()
    coding()
    println("잠을 잔다.")
}
```
이처럼 suspend function 은 코루틴을 활용한 복잡한 비동기 코드를 구조화하여 재사용성과 가독성을 위해 사용된다.

이제 suspend function 여러 테스트 케이스를 통해 suspend function 의 여러가지 사용법에 대해 알아보자 😁

> 만약, 코루틴 테스트를 처음 접하는 독자가 있다면 다음 포스팅을 먼저 읽고 오길 추천한다
[코루틴 테스트 쌩기초 탈출하기 💪](https://velog.io/@murjune/kotlin-Coroutine-%EC%BD%94%EB%A3%A8%ED%8B%B4-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%8C%A9%EA%B8%B0%EC%B4%88-%ED%83%88%EC%B6%9C%ED%95%98%EA%B8%B0)


## 1) Suspend 함수는 코루틴이 아니다.

```kotlin
suspend fun suspendFuncA() {
    delay(300)
	println("Hello")
}

suspend fun suspendFuncB() {
	delay(200)
	println("Odooong")
}
```

간단한 suspend 함수 `suspendFuncA` , `suspendFuncA` 가 있다. 각각, 300ms, 200ms delay를 주었다.

```kotlin
@Test
fun `Suspend 함수는 코루틴이 아니다`() = runTest {
    val job = launch {
        suspendFuncA()
        suspendFuncB()
    }
    advanceUntilIdle() // 현재 testScope 내부 코루틴 작업이 모두 끝날 때까지 대기
    currentTime shouldBe 500
    // output: Hello Odooong
}
```  
해당 테스트는 몇 초 뒤에 종료될까? 🤔 300ms? 500ms?    
한 번 생각해보시죠 ㅎ ㅎ
.
.
.
.
`suspendFuncA()` 과 `suspendFuncB()` 가 서로 독립적인 코루틴이라 생각하고, suspend 함수들이 비동기적으로 실행되어 `300ms` 만큼 시간이 걸릴 것이라 예상한 독자들도 있을 것이다.


![](https://velog.velcdn.com/images/murjune/post/c7711cff-34ee-43bc-9f8a-dd889bcb87a4/image.png)


그러나, 해당 코드는 위 그림처럼 실행된다.  
** suspend function 이 종료될 때까지 호출부 코루틴(runBlocking)은 blocking ** 되기에 해당 테스트는 총 `500ms` 만큼 시간이 걸린다.

위 테스트 코드는 아래 코드와 완전히 동일하다.

```kotlin
@Test
fun `suspend function 은 코드 블록에 불과하다 - 위 테스트 함수와 완전히 동일`() = runTest {
    val job = launch {
    	delay(300)
		println("Hello")
		delay(200)
		println("Odooong")
    }
    advanceUntilIdle() 
    currentTime shouldBe 500
    // output: Hello Odooong
}
```

#### Suspend function 은 코루틴이 아니다. 호출부 코루틴 내에서 돌아가는 중단 가능한 코드 블럭에 불과하는 점을 잊지 말자

> Intro 에서 다룬 예제와 비슷한데 자주 오해할 수 있는 내용이라 한 번 더 강조하기 위해 다뤘다.

## 2) suspend function 에서 병렬 처리할 때, CoroutineScope를  사용하지 말자

이번에는 suspendFuncA() 외 suspendFuncB() 를 병렬처리 해보자

```kotlin
@Test
fun `비동기 처리 - 동시성`() = runTest {
    val job = launch {
		val childA = launch {
			suspendFuncA()
        }
        val childB = launch {
            suspendFuncB()
        }
    }
    advanceUntilIdle()
    currentTime shouldBe 300
    // output: Odooong Hello
}
```

빌드 시 suspendFuncA()와 suspendFuncB() 를 동시에 실행시키기 때문에 해당 테스트는 `300ms` 만큼 걸린다.



이때, 인덴트 깊이가 늘어나는 것이 가독성을 해친다고 생각하여 다음과 같이 `launch{}` 을 suspend 함수 내로 분리하고 싶은 욕구가 들 수도 있다.

<p ailgn="center">
<img src="https://velog.velcdn.com/images/murjune/post/941a3b7d-5d13-41bd-b336-0dd88a891b3b/image.png" width=400>
  </p>



그러나, launch() 함수는 CoroutineScope의 확장 함수이기에 suspend function에서 바로 호출이 불가능하다. launch() 함수를 사용하기 위해서는 새로운 [CoroutineScope](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/) 를 통해 열어줘야 한다.

<p ailgn="center">
<img src="https://velog.velcdn.com/images/murjune/post/39975535-ed36-426e-9d02-ff61881d025d/image.png" width=400>
  </p>


**여기서, 많은 사람들이 CoroutineScope 를 사용하는 실수를 한다.  **

<p ailgn="center">
<img src="https://velog.velcdn.com/images/murjune/post/5b109f85-e156-4655-9a08-fa29c56e6c8e/image.png" width=400>
  </p>

CoroutineScope 를 사용하면서 구조화된 동시성을 유지해주기 위해선 [CoroutineContext](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html) 를 넣어줘야한다. 이때, 다음과 같이 `Dispatcher.IO` 혹은 `EmptyCoroutineContext` 를 넣어주곤 한다.

<p ailgn="center">
<img src="https://velog.velcdn.com/images/murjune/post/448106f5-da52-4ba4-8d4a-f7b1161ac61a/image.png" width=400>
  </p>

해당 코드는 문제가 없을까?

![](https://velog.velcdn.com/images/murjune/post/75a4eb30-cadf-40aa-88fa-a420377582b8/image.png)

❌ 아니다. 해당 코드에는 2가지 문제점이 있다.
- 1) 호출자의 코루틴과 구조화된 동시성이 깨진다
- 2) suspend function 이 종료되어도 코루틴은 동작한다.(즉, 비동기적으로 코드가 동작한다)

CoroutineScope()은 매개변수에 호출자의 [Job](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/) or Job을 포함하는 CoroutineContext를 넘겨주지 않으면, 호출부 코루틴과 독립적인 코루틴 환경을 구축한다.

<p ailgn="center">
<img src="https://velog.velcdn.com/images/murjune/post/a104303e-8bc8-4658-972e-414ef2ccef40/image.png" width=200>
  </p>


따라서, 위 그림처럼 호출부의 코루틴과의 구조화된 동시성이 깨지게 된다.

```kotlin
// suspend 키워드도 빼도 된다
fun suspendFunc() {
    val independantJob = CoroutineScope(Dispatchers.IO).launch {
        delay(200)
        println("Hellow Odooong")
    }
}

@Test
fun `다른 코루틴 디스패처를 사용하면 구조화된 동시성이 깨져, 독립된 코루틴이 된다`() = runTest {
	// runTest 과 아래 suspendFunc() 에서 생성된 코루틴은 별개이다.
	suspendFunc()
    advanceUntilIdle()
    currentTime shouldBe 0
    // Hellow Odooong 이 호출되지 않음
}
```

부모 코루틴은 자식 코루틴이 모두 종료될때까지 대기하는 특성을 가지고 있다. 그러나, CoroutineScope를 사용했기에 runTest 코루틴(부모 코루틴)과의 구조를 깨버렸기에 runTest 코루틴은 independantJob이 끝날 때까지 대기하지 않는다.

<p ailgn="center">
<img src="https://velog.velcdn.com/images/murjune/post/845879e1-16f8-4739-93e4-dcbc06e0b31f/image.png" width=400>
  </p>


테스트를 실행시켜보면 `advanceUntilIdle()` 를 호출했음에도 runTest 가 바로 종료되는 것을 볼 수 있다.

- coroutineContext 활용


```kotlin
suspend fun suspendFuncAWithCoroutineScope() {
    CoroutineScope(coroutineContext + CoroutineName("ChildA")).launch {
        delay(300)
        print(" Hello ")
    }
}

suspend fun suspendFuncBWithCoroutineScope() {
    CoroutineScope(coroutineContext + CoroutineName("ChildB")).launch {
        delay(200)
        print(" Odooong ")
    }
}
```  
coroutineContext property 를 활용하면 현재 실행되고 있는 코루틴의 context를 가져올 수 있다. CoroutineScope 에 `coroutineContext`를 넣어주면 runTest 코루틴과 `구조화된 동시성`을 유지할 수 있다.

<p ailgn="center">
<img src="https://velog.velcdn.com/images/murjune/post/a194b620-9c54-4bbb-908f-e7b172322c87/image.png" width=400>
  </p>


그럼 테스트 코드를 다음과 같이 깔끔하게 나타낼 수 있다.

```kotlin
@Test
fun `suspend 함수 내에 코루틴 스코프를 열어 자식 코루틴 생성 - 동시성`() = runTest {
	suspendFuncAWithCoroutineScope()
	suspendFuncBWithCoroutineScope()
    advanceUntilIdle()
    currentTime shouldBe 300
    // Output: Odooong Hello
}
```

> 휴! 시간도 `300ms` 로 단축했고, 가독성도 챙겼으니 해당 코드는 좋은 코드일까? 🤔

![](https://velog.velcdn.com/images/murjune/post/75a4eb30-cadf-40aa-88fa-a420377582b8/image.png)

❌ 언듯 보기에는 좋아보일 수 있으나, 잘못 설계한 것이다.  
동료개발자는 suspend 함수가 종료되는 시점에 내부 작업들이 끝났을 것이라 예상할 것이다.

<p ailgn="center">
<img src="https://velog.velcdn.com/images/murjune/post/2d45c6e3-2d33-4ae8-bf90-20d909efaab8/image.png" width=400>
  </p>

그러나, 실상은 그림과 같이 suspend 함수는 자식 코루틴을 만들고 바로 종료되고, 자식 코루틴들은 비동기적으로 실행되고 있다.



이는 심각한 버그의 원인이 될 수 있으며, 어디서 발생한 버그인지 찾기도 매우 힘들다.🥲

> 위 코드와 비슷한 형태로 설계된 코드가 때문에 버그가 발생하여 쌩고생한 경험이 있다 🥲

- ** suspend function 에서 CoroutineScope 를 사용하지 말자 **
- ** suspend function 실행이 종료되었을 때, 내부 코드의 실행이 완료되도록 설계하자! **

## 3) coroutineScope or withContext 함수를 활용하자!⭐️

suspend function 내부 동작을 병렬 처리하고 싶다면, [coroutineScope](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/coroutine-scope.html) 를 활용하자!(대문자 CoroutineScope 말고 소문자 coroutineScope 라는 함수가 있다)

```kotlin
@Test
fun `suspend 함수 분리 - 동시성`() = runTest {
    mergedSuspendFunc()
    currentTime shouldBe 300
    // Output: Odooong Hello
}

suspend fun mergedSuspendFunc() = coroutineScope {
    launch(CoroutineName("ChildA")) {
        suspendFuncA()
    }
    launch(CoroutineName("ChildB")) {
        suspendFuncB()
    }
}
```
coroutineScope를 사용하면 `runTest 코루틴`는 `mergedSuspendFunc()` 가 종료될 때까지 대기하고, `mergedSuspendFunc()` 내부에서는 병렬적으로 코드를 실행하도록 할 수 있다.

<p ailgn="center">
<img src="https://velog.velcdn.com/images/murjune/post/662f6752-75bc-4ccf-b883-c6a42bc24232/image.png" width=400>
  </p>

코루틴 관계도는 다음과 같다

<p ailgn="center">
<img src="https://velog.velcdn.com/images/murjune/post/931188f2-790e-4cf3-8e42-83fa282388b6/image.png" width=400>
  </p>

코드 구조와 시간적 효율성 2마리 토끼를 모두 잡을 수 있게 되었다 😁

> [coroutineScope](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/coroutine-scope.html) 의 특성
> - 호출부의 `coroutineContext` 를 상속받는 Job을 생성하기에 코루틴의 구조화된 동시성을 깨지 않는다.
- 자식 코루틴이 모두 끝날때까지 호출부의 코루틴을 blocking 시킨다는 특징이 있다.(suspend function과 찰떡궁합이다.)
- 코드 블럭의 마지막 값을 return 한다.

  만약, 다른 디스패처를 활용하고 싶다면 [withContext](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/with-context.html)를 활용해주면 된다. 디스패터를 지정해줄 수 있다는 점을 제외하고 coroutineScope와 동일한 기능을 한다.

```kotlin
suspend fun mergedSuspendFunc() = withContext(Dispatcher.IO) {
    launch {
        suspendFuncA()
    }
    launch {
        suspendFuncB()
    }
}
```

---

suspend 함수 내부에서 **병렬 처리를 하고 결과값을 반환**해야 할 경우가 종종 생긴다. 이런 경우 coroutineScope 와 [async](https://www.google.com/search?q=async+coroutine&sca_esv=94e45fce1d51b060&sxsrf=ADLYWIJ8zw8Iclt335PJ9p5eyK67-80qfw%3A1719349228477&ei=7C97Zv_qHKu1vr0PkYCUgAs&ved=0ahUKEwj_kIiA0_eGAxWrmq8BHREABbAQ4dUDCA8&uact=5&oq=async+coroutine&gs_lp=Egxnd3Mtd2l6LXNlcnAiD2FzeW5jIGNvcm91dGluZTIFEAAYgAQyBRAAGIAEMgUQABiABDIEEAAYHjIGEAAYHhgPMgQQABgeMgQQABgeMgYQABgFGB4yBhAAGAUYHjIGEAAYCBgeSO8bUNACWPIacAN4AZABAZgB2QKgAbwUqgEHMS43LjQuMrgBA8gBAPgBAZgCD6ACqhDCAgoQABiwAxjWBBhHwgILEAAYgAQYsQMYgwHCAggQABiABBiiBJgDAIgGAZAGCpIHBzQuNy4zLjGgB9pI&sclient=gws-wiz-serp)  를 함께 사용하면 된다.(Like 분할정복)


우테코 선릉 캠퍼스 크루들을 fetch 해오는 예시를 통해 알아보자!
```kotlin
suspend fun fetchWootecoAndroidCrews(): List<String> {
    delay(300)
    return listOf("오둥이", "꼬상", "하디", "팡태", "악어", "케이엠")
}

suspend fun fetchWootecoFrontendCrews(): List<String> {
    delay(200)
    return listOf("토다리", "제이드")
}
```
안드 크루와 프론트 크루를 불러오는 api가 있다
두 함수의 실행 결과는 서로 독립적이기 때문에 병렬 처리하기에 매우 적합하다 😁

```kotlin
suspend fun fetchWootecoCrews(): List<String> = coroutineScope {
        val androidJob = async { fetchWootecoAndroidCrews() }
        val frontJob = async { fetchWootecoFrontendCrews() }
        // 결과값 반환
        androidJob.await() + frontJob.await()
}
```
따라서, `async{}`로 각 함수가 서로 다른 코루틴에서 실행되도록 묶어준 후, 결과값을 반환해주는 부분에 `await()`를 호출해준다.

```kotlin
@Test
fun `우테코 선릉 캠퍼스 크루들 불러오기`() = runTest {
    val crews = fetchWootecoCrews()
    currentTime shouldBe  300
    crews shouldContainExactlyInAnyOrder listOf("오둥이", "꼬상", "하디", "팡태", "악어", "케이엠", "토다리", "제이드")
}
```

이처럼 어떤 값을 반환하고 내부적으로 병렬처리를 하는 suspend function 을 설계할 때 `coroutineScope + async`를 활용해보자 😁

필자는 우테코 쇼핑 주문하기 미션에서 적용가능한 쿠폰을 불러오는 UseCase 에서 `coroutineScope + async` 를 활용하여 병렬처리를 적용해본 적이 있다.

[해당 코드](https://github.com/murjune/android-shopping-order/blob/step2/app/src/main/java/woowacourse/shopping/domain/usecase/order/LoadAvailableDiscountCouponsUseCase.kt)

## 4) supervisorScope 사용을 고려해보자(심화)

suspend function 내에서 `coroutineScope` 와 `async/launch` 를 활용하여 여러 api 들을 병렬 처리할 때 한가지 제약이 있다.
100개의 api를 통합하는 suspend function이 있고, 자식 코루틴이 한개라도 exception이 터진다면 예외가 전파되어 모든 코루틴이 cancel된다는 것이다.

이때, 기획에서는 통신에 성공한 데이터라도 불러와달라고 요청을 했다! 그럼 어떻게 처리하는 것이 적절할까?
이번에는 우테코 코치님들을 불러오는 예시를 통해 알아보자

```kotlin
suspend fun fetchAndroidCoaches(): List<String> {
    delay(50)
    return listOf("제이슨", "레아", "제임스")
}

suspend fun fetchFrontCoaches(): List<String> {
    delay(150)
    return listOf("준", "크론")
}

suspend fun fetchBackCoaches(): List<String> {
    delay(70)
    throw NoSuchElementException("제가 백엔드 코치님들은 모릅니다..")
}


```
현재 `fetchBackCoaches()` 함수에서 NoSuchElementException 예외를 발생시키고 있다.

```kotlin
@Test
fun `하나라도 예외가 발생하면, 모든 작업이 취소된다`() = runTest {
    shouldThrow<NoSuchElementException> {
        fetchErrorWootecoCoaches()
    }
    currentTime shouldBe 70
}

suspend fun fetchWootecoCoaches() = coroutineScope {
    val androidJob = async { fetchAndroidCoaches() }
    val frontJob = async { fetchFrontCoaches() }
    val backendJob = async { fetchBackCoaches() }
    androidJob.await() + frontJob.await() + backendJob.await()
}
```

<p ailgn="center">
<img src="https://velog.velcdn.com/images/murjune/post/ac8d4678-dc26-4e0c-bfc0-d5085006653a/image.png" width=400>
  </p>  


`fetchWootecoCoaches()` 를 불러올 때 예외가 `fetchBackCoaches -> coroutineScope -> runTest` 로 예외가 전파되며, 모든 작업들은 취소가 된다.




이럴 때 에러 전파를 방지하기 위해 [supervisorScope{}](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/supervisor-scope.html) 를 활용할 수 있다.

> supervisorScope 는 coroutineScope 와 거의 똑같지만, 자식 코루틴의 예외 전파를 차단시킨다는 특성이 있다.

이를 활용하여 다음과 같이 개선할 수 있다.
```kotlin
suspend fun fetchWootecoCoaches() = supervisorScope { 
    val androidJob = async { fetchAndroidCoaches() }
    val frontJob = async { fetchFrontCoaches() }
    val backendJob = async { fetchBackCoaches() }
    // result
    val backendResult = runCatching { backendJob.await() }
    androidJob.await() + frontJob.await() + backendResult.getOrDefault(emptyList())
}
```
기존 coroutineScope가 supervisorScope로 교체된 것 외에도, `await()` 하는 부분에서 runCatching으로 예외 처리를 해주는 것을 볼 수 있다.

async 으로 인해 만들어진 코루틴에서 Exception이 발생하면 `1) 전파되는 예외 + 2) await() 에서 발생하는 예외`가 모두 처리 해줘야하기 때문이다.

```kotlin
@Test
fun `supervisorScope 를 활용하여 Error 전파 방지`() = runTest {
    val coaches = fetchWootecoCoaches()
    currentTime shouldBe 150
    coaches shouldContainExactlyInAnyOrder listOf("제이슨", "레아", "제임스", "준", "크론")
}
```  

<p ailgn="center">
<img src="https://velog.velcdn.com/images/murjune/post/75b078ae-d707-4c26-9ad9-2bc87f575c95/image.png" width=400>
  </p>  


여러 코루틴 성공한 값들만 불러와 반환하도록 개선해주었다!

## 정리

>
- suspend function 코루틴이 아니다.
- suspend function 은 호출부 코루틴의 코드블럭이다.
- suspend function 이 종료될 때, 내부 실행 코드도 종료되도록 설계하자
- suspend function 내부에서 병렬 처리를 할 떄, 구조화된 동시성을 보장해주기 위해 coroutineScope/withContext 를 사용하자
- 자식 코루틴의 예외 전파를 방지하고 싶다면, coroutineScope 대신 supervisorScope를 사용하자


긴 글 읽어주셔서 감사합니다.  
이해가 안되거나 피드백 주실 부분이 있다면 편하게 댓글로 부탁드려요!!  🙇‍♂️
