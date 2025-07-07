package com.murjune.practice.rxjava2.basic

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * Observer의 기본 개념과 구독 방법을 학습한다.
 * 
 * 학습 목표:
 * 1. Observer 인터페이스의 4가지 메소드 이해
 * 2. 다양한 구독(subscribe) 방법들
 * 3. CompositeDisposable을 통한 구독 관리
 * 4. 메모리 누수 방지 패턴
 */
class ObserverBasicTest : BehaviorSpec({

    lateinit var testObserver: TestObserver<Any>
    lateinit var testScheduler: TestScheduler

    beforeTest {
        testObserver = TestObserver<Any>()
        testScheduler = TestScheduler()
    }

    Given("커스텀 Observer") {
        val items = mutableListOf<String>()
        var isSubscribeCalled = false
        var isErrorCalled = false
        var isCompleted = false

        val customObserver = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                isSubscribeCalled = true // 구독 시작 시 호출
            }

            override fun onNext(t: String) {
                items.add(t)
            }

            override fun onError(e: Throwable) {
                isErrorCalled = true // 에러 발생 시 호출
            }

            override fun onComplete() {
                isCompleted = true // 모든 데이터 방출 완료 시 호출
            }
        }
        When("정상적인 데이터 방출") {

            Then("onError() 를 제외한 모든 콜백이 정상 호출되어야 한다") {
                Observable.just("A", "B", "C")
                    .subscribe(customObserver)

                isSubscribeCalled.shouldBeTrue()
                items shouldBe listOf("A", "B", "C")
                isErrorCalled.shouldBeFalse()
                isCompleted.shouldBeTrue()
            }
        }

        When("에러 발생") {
            val errorMessage = "테스트 에러"

            Then("onComplete() 를제외한 모든 콜백이 정상 호출되어야 한다") {
                Observable.error<String>(IllegalStateException(errorMessage))
                    .subscribe(customObserver)

                isSubscribeCalled.shouldBeTrue()
                items shouldBe emptyList()
                isErrorCalled.shouldBeTrue()
                isCompleted.shouldBeFalse()
            }
        }
    }

    Given("Observer 인터페이스 구현") {
        
        When("모든 콜백을 구현한 Observer") {
            val results = mutableListOf<String>()
            val observer = object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    results.add("onSubscribe")
                }
                
                override fun onNext(t: String) {
                    results.add("onNext: $t")
                }
                
                override fun onError(e: Throwable) {
                    results.add("onError: ${e.message}")
                }
                
                override fun onComplete() {
                    results.add("onComplete")
                }
            }
            
            Then("모든 콜백이 순서대로 호출되어야 한다") {
                Observable.just("A", "B")
                    .subscribe(observer)
                
                results shouldBe listOf(
                    "onSubscribe",
                    "onNext: A",
                    "onNext: B",
                    "onComplete"
                )
            }
        }
        
        When("에러가 발생하는 Observer") {
            val results = mutableListOf<String>()
            val observer = object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    results.add("onSubscribe")
                }
                
                override fun onNext(t: String) {
                    results.add("onNext: $t")
                }
                
                override fun onError(e: Throwable) {
                    results.add("onError: ${e.message}")
                }
                
                override fun onComplete() {
                    results.add("onComplete")
                }
            }
            
            Then("에러 발생 시 onComplete는 호출되지 않아야 한다") {
                Observable.create<String> { emitter ->
                    emitter.onNext("A")
                    emitter.onError(RuntimeException("테스트 에러"))
                    emitter.onNext("B")  // 에러 후 호출되지 않음
                    emitter.onComplete()  // 에러 후 호출되지 않음
                }.subscribe(observer)
                
                results shouldBe listOf(
                    "onSubscribe",
                    "onNext: A",
                    "onError: 테스트 에러"
                )
            }
        }
    }
    
    Given("다양한 구독 방법") {
        
        When("람다로 구독 (onNext만)") {
            val receivedValues = mutableListOf<String>()
            
            Then("onNext만 처리해야 한다") {
                Observable.just("Hello", "World")
                    .subscribe { value ->
                        receivedValues.add(value)
                    }
                
                receivedValues shouldBe listOf("Hello", "World")
            }
        }
        
        When("람다로 구독 (onNext + onError)") {
            val receivedValues = mutableListOf<String>()
            val errorRef = AtomicReference<Throwable?>()
            
            Then("정상 처리 시 onNext만 호출되어야 한다") {
                Observable.just("A", "B", "C")
                    .subscribe(
                        { value -> receivedValues.add(value) },
                        { error -> errorRef.set(error) }
                    )
                
                receivedValues shouldBe listOf("A", "B", "C")
                errorRef.get() shouldBe null
            }
        }
        
        When("람다로 구독 (onNext + onError + onComplete)") {
            val receivedValues = mutableListOf<String>()
            val errorRef = AtomicReference<Throwable?>()
            val completedRef = AtomicReference<Boolean>(false)
            
            Then("모든 콜백이 적절히 호출되어야 한다") {
                Observable.just("X", "Y")
                    .subscribe(
                        { value -> receivedValues.add(value) },
                        { error -> errorRef.set(error) },
                        { completedRef.set(true) }
                    )
                
                receivedValues shouldBe listOf("X", "Y")
                errorRef.get() shouldBe null
                completedRef.get() shouldBe true
            }
        }
    }
    
    Given("CompositeDisposable을 사용한 구독 관리") {
        
        When("여러 구독을 관리") {
            val compositeDisposable = CompositeDisposable()
            val results = mutableListOf<String>()
            
            Then("모든 구독이 정상 처리되어야 한다") {
                val d1 = Observable.just("A")
                    .subscribe { results.add("First: $it") }
                
                val d2 = Observable.just("B")
                    .subscribe { results.add("Second: $it") }
                
                compositeDisposable.addAll(d1, d2)
                
                results shouldBe listOf("First: A", "Second: B")
                compositeDisposable.size() shouldBe 2
            }
        }
        
        When("CompositeDisposable 일괄 해제") {
            val compositeDisposable = CompositeDisposable()
            val processCount = AtomicInteger(0)
            
            Then("모든 구독이 해제되어야 한다") {
                // 긴 처리 시뮬레이션
                val d1 = Observable.range(1, 1000)
                    .doOnNext { processCount.incrementAndGet() }
                    .subscribe()
                
                val d2 = Observable.range(1, 1000)
                    .doOnNext { processCount.incrementAndGet() }
                    .subscribe()
                
                compositeDisposable.addAll(d1, d2)
                
                // 즉시 해제
                compositeDisposable.dispose()
                
                d1.isDisposed shouldBe true
                d2.isDisposed shouldBe true
                compositeDisposable.isDisposed shouldBe true
            }
        }
        
        When("CompositeDisposable 초기화") {
            val compositeDisposable = CompositeDisposable()
            
            Then("clear() 후 새로운 구독 추가 가능해야 한다") {
                val d1 = Observable.just("A").subscribe()
                compositeDisposable.add(d1)
                
                compositeDisposable.size() shouldBe 1
                
                // clear는 구독을 해제하지만 CompositeDisposable은 재사용 가능
                compositeDisposable.clear()
                
                compositeDisposable.size() shouldBe 0
                compositeDisposable.isDisposed shouldBe false
                
                // 새로운 구독 추가 가능
                val d2 = Observable.just("B").subscribe()
                compositeDisposable.add(d2)
                
                compositeDisposable.size() shouldBe 1
            }
        }
    }
    
    Given("메모리 누수 방지 패턴") {
        
        When("안드로이드 생명주기와 구독 관리") {
            val compositeDisposable = CompositeDisposable()
            val results = mutableListOf<String>()
            
            // 안드로이드 Activity/Fragment onResume에서 구독 시작
            fun onResume() {
                val disposable = Observable.interval(100, java.util.concurrent.TimeUnit.MILLISECONDS)
                    .take(5)
                    .map { "Data $it" }
                    .subscribe { results.add(it) }
                
                compositeDisposable.add(disposable)
            }
            
            // 안드로이드 Activity/Fragment onPause에서 구독 해제
            fun onPause() {
                compositeDisposable.clear()
            }
            
            Then("생명주기에 따라 구독이 관리되어야 한다") {
                onResume()


                // 구독이 활성 상태
                compositeDisposable.size() shouldBe 1
                
                // 데이터 처리 시간 대기
                Thread.sleep(600)
                
                onPause()
                
                // 구독이 해제됨
                compositeDisposable.size() shouldBe 0
                
                // 5개의 데이터가 모두 처리되었는지 확인
                results.size shouldBe 5
            }
        }
    }
})