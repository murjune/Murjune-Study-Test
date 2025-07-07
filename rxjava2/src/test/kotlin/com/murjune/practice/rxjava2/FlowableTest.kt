package com.murjune.practice.rxjava2

import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subscribers.TestSubscriber
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import kotlin.test.Test

/**
 * [Flowable]는  cold stream 이다 (Like, [Flow])
 *
 * [Observable] 과 차이는 [Flowable] 는 배압 조절이 가능하다.
 *
 * BackpressureStrategy 는 추후 필요할 때 학습하도록 하자! (Flow 랑 비슷함~)
 * */
class FlowableTest {

    @Test
    fun testSimpleFlowable() {
        // given
        val testSubscriber = TestSubscriber<String>()
        val flowable = Flowable.just("A", "B", "C")

        // when
        flowable.subscribe(testSubscriber)

        // then
        testSubscriber.assertValues("A", "B", "C")
        testSubscriber.assertComplete()
        testSubscriber.assertNoErrors()
    }


    @Test
    fun testFlowableWithOperator() {
        // given
        val testSubscriber = TestSubscriber<String>()
        val flowable = Flowable.range(1, 10)
            .filter { it % 2 == 0 }
            .map { "Even $it" }

        // when
        flowable.subscribe(testSubscriber)

        // then
        testSubscriber.assertValues("Even 2", "Even 4", "Even 6", "Even 8", "Even 10")
        testSubscriber.assertComplete()
    }

    @Test
    fun testCombineLatest() {
        val f1 = Flowable.interval(100, TimeUnit.MILLISECONDS).map { "A$it" }
        val f2 = Flowable.interval(150, TimeUnit.MILLISECONDS).map { "B$it" }

        Flowable.combineLatest(f1, f2) { a, b -> "$a-$b" }
            .take(5)
            .blockingSubscribe { println(it) }
    }

    @Test
    fun testSubscribeWith() {
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
    }


    /**
     * 연산자	설명
     * [Flowable.subscribe]
     *  - "한 번"만 적용
     *  - 발행자(publisher)가 "실행" 되는 스레드를 지정
     *  - 데이터 생성이 어느 스레드에서 일어날지를 정함.
     *
     * [Flowable.observeOn] Like. [Flow.flowOn]
     *  - 이후 연산자부터 구독하는 쪽의 스레드 지정 (여러 번 가능)
     *  - 구독자(observer)가 "동작" 하는 스레드를 지정
     *  - onNext, onComplete, onError가 호출되는 스레드를 정함.
     * */
}