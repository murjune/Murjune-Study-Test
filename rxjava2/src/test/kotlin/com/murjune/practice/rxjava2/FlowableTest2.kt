package com.murjune.practice.rxjava2

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subscribers.TestSubscriber
import java.util.concurrent.TimeUnit
import kotlin.test.Test

/**
 * 개선된 FlowableTest - TestScheduler, 에러 처리, Disposable 관리 포함
 */
class FlowableTest2 {

    @Test
    fun testSimpleFlowableWithTrailingComma() {
        // given
        val testSubscriber = TestSubscriber<String>()
        val flowable = Flowable.just(
            "A",
            "B", 
            "C",
        )

        // when
        flowable.subscribe(testSubscriber)

        // then
        testSubscriber.assertValues("A", "B", "C")
        testSubscriber.assertComplete()
        testSubscriber.assertNoErrors()
    }

    @Test
    fun testFlowableWithOperatorAndErrorHandling() {
        // given
        val testSubscriber = TestSubscriber<String>()
        val flowable = Flowable.range(1, 10)
            .filter { it % 2 == 0 }
            .map { 
                if (it == 6) throw RuntimeException("Test Error")
                "Even $it" 
            }
            .onErrorReturn { "Error occurred" }

        // when
        flowable.subscribe(testSubscriber)

        // then
        testSubscriber.assertValues("Even 2", "Even 4", "Error occurred")
        testSubscriber.assertComplete()
        testSubscriber.assertNoErrors()
    }

    @Test
    fun testCombineLatestWithTestScheduler() {
        // given
        val testScheduler = TestScheduler()
        val testSubscriber = TestSubscriber<String>()
        
        val f1 = Flowable.interval(100, TimeUnit.MILLISECONDS, testScheduler)
            .map { "A$it" }
        val f2 = Flowable.interval(150, TimeUnit.MILLISECONDS, testScheduler)
            .map { "B$it" }

        // when
        Flowable.combineLatest(f1, f2) { a, b -> "$a-$b" }
            .take(3)
            .subscribe(testSubscriber)

        // 시간을 인위적으로 진행시킴 (실제 시간 기다리지 않음)
        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)

        // then
        testSubscriber.assertValueCount(3)
        testSubscriber.assertNotComplete() // take(3) 후에도 스트림은 계속됨
        testSubscriber.values().forEach { println("Combined: $it") }
    }

    @Test
    fun testSubscribeWithAndDisposableManagement() {
        // given
        val testSubscriber = TestSubscriber<String>()
        val flowable = Flowable.range(1, 10)
            .filter { it % 2 == 0 }
            .map { "Even $it" }

        // when
        val returned = flowable.subscribeWith(testSubscriber)

        // then
        testSubscriber shouldBeSameInstanceAs returned
        testSubscriber.assertValues("Even 2", "Even 4", "Even 6", "Even 8", "Even 10")
        testSubscriber.assertComplete()
        
        // Disposable 상태 확인
        returned.isDisposed shouldBe true // TestSubscriber는 완료되면 자동으로 dispose됨
    }

    @Test
    fun testCompositeDisposableManagement() {
        // given
        val compositeDisposable = CompositeDisposable()
        val results = mutableListOf<String>()
        
        val flowable1 = Flowable.just("A", "B", "C")
        val flowable2 = Flowable.just("1", "2", "3")

        // when
        val disposable1 = flowable1.subscribe { results.add("First: $it") }
        val disposable2 = flowable2.subscribe { results.add("Second: $it") }
        
        compositeDisposable.addAll(disposable1, disposable2)

        // then
        results.size shouldBe 6
        compositeDisposable.size() shouldBe 2
        
        // 모든 구독 한 번에 해제
        compositeDisposable.dispose()
        
        compositeDisposable.isDisposed shouldBe true
    }

    @Test
    fun testFlowableWithSchedulers() {
        // given
        val testScheduler = TestScheduler()
        val testSubscriber = TestSubscriber<String>()
        
        // when
        Flowable.fromCallable {
            Thread.sleep(100) // 무거운 작업 시뮬레이션
            "Heavy work result"
        }
        .subscribeOn(testScheduler) // 작업 스레드 지정
        .observeOn(testScheduler)   // 결과 받을 스레드 지정
        .subscribe(testSubscriber)

        // 스케줄러가 작업을 실행하도록 함
        testScheduler.triggerActions()

        // then
        testSubscriber.assertValue("Heavy work result")
        testSubscriber.assertComplete()
    }

    @Test
    fun testBackpressureHandling() {
        // given
        val testSubscriber = TestSubscriber<Int>()
        
        // when
        Flowable.range(1, 1000)
            .onBackpressureBuffer(10) // 버퍼 크기 제한
            .observeOn(io.reactivex.schedulers.Schedulers.computation())
            .subscribe(testSubscriber)

        // then
        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertComplete()
        testSubscriber.assertValueCount(1000)
    }

    @Test
    fun testFlowableErrorHandling() {
        // given
        val testSubscriber = TestSubscriber<String>()
        
        // when
        Flowable.fromCallable {
            throw RuntimeException("Intentional error")
        }
//        .onErrorResumeNext { error: Throwable ->
//            Flowable.just("Recovered from: ${error.message}")
//        }
        .subscribe(testSubscriber)

        // then
        testSubscriber.assertValue("Recovered from: Intentional error")
        testSubscriber.assertComplete()
        testSubscriber.assertNoErrors()
    }

    /**
     * subscribeOn vs observeOn 차이점 테스트
     * 
     * subscribeOn: 구독(데이터 생성) 스레드 지정 (한 번만 적용)
     * observeOn: 관찰(데이터 처리) 스레드 지정 (여러 번 적용 가능)
     */
    @Test
    fun testSubscribeOnVsObserveOn() {
        // given
        val testScheduler1 = TestScheduler()
        val testScheduler2 = TestScheduler()
        val results = mutableListOf<String>()

        // when
        Flowable.fromCallable {
            "Created on: ${Thread.currentThread().name}"
        }
        .subscribeOn(testScheduler1)  // 생성 스레드
        .map { "$it -> Mapped on: ${Thread.currentThread().name}" }
        .observeOn(testScheduler2)    // 이후 처리 스레드
        .subscribe { results.add(it) }

        // 각 스케줄러 실행
        testScheduler1.triggerActions()
        testScheduler2.triggerActions()

        // then
        results.size shouldBe 1
        println("Result: ${results.first()}")
    }
}