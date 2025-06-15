# RxJava2 Basic 개념

RxJava2의 핵심 구성 요소인 Observable과 Observer의 기본 개념을 학습합니다.

## 1. Observable (관찰 가능한 객체)

### 정의
- **데이터 스트림을 방출하는 객체**
- 0개 이상의 데이터를 비동기적으로 방출
- 완료(onComplete) 또는 에러(onError)로 스트림 종료

### 주요 생성 방법

```kotlin
// 1. 단일 값
Observable.just("Hello")

// 2. 여러 값
Observable.just("A", "B", "C")

// 3. 배열에서 생성
Observable.fromArray(1, 2, 3, 4, 5)

// 4. 범위 생성
Observable.range(1, 5)  // 1부터 5개

// 5. 빈 Observable
Observable.empty<String>()

// 6. 에러 Observable
Observable.error<String>(RuntimeException("에러"))
```

### 특징
- **Cold Stream**: 구독할 때마다 새로운 데이터 스트림 생성
- **백프레셔 미지원**: 데이터 생산 속도 제어 불가 (Flowable과 차이점)
- **Push 기반**: Observer에게 데이터를 푸시

## 2. Observer (관찰자)

### 정의
- **Observable이 방출하는 데이터를 수신하는 객체**
- 4개의 콜백 메소드로 구성

### Observer 인터페이스

```kotlin
interface Observer<T> {
    fun onSubscribe(d: Disposable)  // 구독 시작
    fun onNext(t: T)               // 데이터 수신
    fun onError(e: Throwable)      // 에러 발생
    fun onComplete()               // 완료
}
```

### 콜백 호출 순서
1. **onSubscribe**: 구독 시작 시 1번 호출
2. **onNext**: 데이터별로 0~N번 호출
3. **onError** 또는 **onComplete**: 스트림 종료 시 1번 호출
   - 둘 중 하나만 호출됨 (상호 배타적)

## 3. 구독 (Subscribe)

### 구독 방법들

```kotlin
// 1. Observer 인터페이스 구현
val observer = object : Observer<String> { ... }
observable.subscribe(observer)

// 2. 람다로 구독 (onNext만)
observable.subscribe { value -> // onNext 
    println(value) 
}

// 3. 람다로 구독 (onNext + onError)
observable.subscribe(
    { value -> println(value) }, // onNext
    { error -> println("Error: $error") } // onError
)

// 4. 람다로 구독 (onNext + onError + onComplete)
observable.subscribe(
    { value -> println(value) }, // onNext
    { error -> println("Error: $error") }, // onError
    { println("Complete") } // onComplete
)
```

## 4. Disposable (구독 해제)

RxJava2에서 Disposable은 Observable 등과 같은 데이터 스트림에 대한 구독을 관리하는 객체다.
주요 목적은 **구독 해제**(unsubscribe) 를 통해 다음과 같은 작업을 수행하는 것이다:
- 1. **메모리 누수 방지**: 불필요한 리소스 해제
- 2. **성능 최적화**: 불필요한 연산 중단

```
🤔 왜 메모리 누수가 발생되나요?

만약, 내가 구독한 Observable에 대해 원하는 값을 받았고 더이상 값을 받을 필요가 없다면,
그 구독을 계속 하고 있는 것은 불필요한 리소스를 소모하는 것이다.

현실세계로 비유를 들자면, 어벤져스를 보기 위해 넷플릭스를 매달 1만원씩 내고 구독하고 있다고 해보자.
그런데, 내가 어벤져스를 모두 시청해서 넷플릭스를 구독할 이유가 없어진 상황이다.

그런데도 계속 구독을 하고 있다면, 매달 1만원씩 낭비하는 것과 같다.

이와 비슷하게 RxJava2에서 Observable을 구독하고 나서, 
불필요한 구독을 계속 유지하고 있다면 메모리 누수나 불필요한 연산이 발생할 수 있다.
 
```

`CompositeDisposable` 를 활용해서 여러 개의 Disposable을 한번에 관리할 수 있다.

### 사용 방법

```kotlin
// 1. 단일 구독 관리
val disposable = observable.subscribe { ... }
disposable.dispose()  // 구독 해제

// 2. 여러 구독 관리 (CompositeDisposable)
val compositeDisposable = CompositeDisposable()

val d1 = observable1.subscribe { ... }
val d2 = observable2.subscribe { ... }

compositeDisposable.addAll(d1, d2)

// 일괄 해제
compositeDisposable.dispose()  // 재사용 불가
// 또는
compositeDisposable.clear()    // 재사용 가능
```

## 5. 안드로이드에서의 활용 패턴

### 생명주기와 연동

```kotlin
class MainActivity : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()
    
    override fun onCreate() {
        super.onResume()
        
        // 네트워크 요청 구독
        val disposable = apiService.getData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { data -> updateUI(data) },
                { error -> showError(error) }
            )
            
        compositeDisposable.add(disposable)
    }
    
    override fun onDestroy() {
        super.onPause()
        compositeDisposable.clear()  // 모든 구독 해제
    }
}
```

## 6. Coroutine/Flow와 비교

| 구분 | RxJava2 Observable | Kotlin Flow |
|------|-------------------|-------------|
| **생성** | `Observable.just()` | `flowOf()` |
| **구독** | `subscribe()` | `collect()` |
| **해제** | `Disposable.dispose()` | `Job.cancel()` |
| **에러처리** | `onError` 콜백 | `try-catch` |
| **백프레셔** | 미지원 (Flowable 필요) | 지원 |
| **스레드 전환** | `subscribeOn/observeOn` | `flowOn` |

## 7. 주요 주의사항

### 메모리 누수 방지
- **반드시 구독 해제**: Activity/Fragment 생명주기에 맞춰 dispose
- **CompositeDisposable 활용**: 여러 구독을 한 번에 관리
- **onDestroy에서 해제**: 최종 안전장치

### 에러 처리
- **onError 구현 필수**: 처리하지 않으면 앱 크래시
- **에러 후 스트림 종료**: onComplete 호출되지 않음
- **재시도 패턴**: `retry()`, `retryWhen()` 활용

### 스레드 안전성
- **메인 스레드 고려**: UI 업데이트는 메인 스레드에서
- **스케줄러 활용**: `subscribeOn()`, `observeOn()` 적절히 사용

이러한 기본 개념을 확실히 이해한 후, 연산자(Operator)와 스케줄러(Scheduler)를 학습하면 RxJava2를 효과적으로 활용할 수 있습니다.