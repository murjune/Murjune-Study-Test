package com.murjune.practice.rxjava2

import io.reactivex.Flowable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.AsyncSubject
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.Subject
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.test.Test


/**
 * TODO : rxJava2 모듈 파서 mv 하기
 *
 * [Subject]는  hot stream 이다 (Like, [SharedFlow], [StateFlow])
 *
 * cold Stream 은 [Flowable]
 * */
class SubjectTest {

    /**
     * [PublishSubject]: 구독한 이후부터의 데이터만 받음
     *
     * SharedFlow 와 동작 비슷
     *
     * e). 버튼 클릭 이벤트 전달
     * */
    @Test
    fun testPublishSubject() {
        // given
        val testObserver = TestObserver<String>()
        val subject = PublishSubject.create<String>()

        // when
        subject.onNext("A") // 구독자 X > 데이터 유실됨

        subject.subscribe(testObserver) // testObserver 가 구독 시작

        subject.onNext("B")
        subject.onNext("C")

        // then
        testObserver.assertValues("B", "C")
    }


    /**
     * [BehaviorSubject]: 가장 최근 값 1개 기억 → 구독하면 즉시 emit
     *
     * StateFlow 와 동작 비슷
     *
     * e). 상태 저장/전달 (UIState 등)
     * */
    @Test
    fun testBehaviorSubject() {
        // given
        val testObserver = TestObserver<String>()
        val defaultValue = "First"
        val subject = BehaviorSubject.createDefault(defaultValue)

        // when
        subject.onNext("Second") // 가장 최근 저장 element : First > Second

        subject.subscribe(testObserver) // 구독 시작

        subject.onNext("Third")

        // then
        testObserver.assertValues("Second", "Third")
    }

    /**
     * [ReplaySubject]: 모든 이전 값을 모두 기억해서, 구독 시 한 번에 전부 전달
     */
    @Test
    fun testReplaySubject() {
        // given
        val testObserver = TestObserver<String>()
        val subject = ReplaySubject.create<String>()

        // when
        subject.onNext("A")
        subject.onNext("B")

        subject.subscribe(testObserver) // 구독 시작

        subject.onNext("C")

        // then
        testObserver.assertValues("A", "B", "C")
    }

    /**
     * [AsyncSubject]: complete() 호출된 시점에 가장 마지막 값을 단 하나만 전달
     *
     * 만약 onComplete() 안 불렀다면 아무것도 안 나옴
     *
     */
    @Test
    fun testAsyncSubject() {
        // given
        val testObserver = TestObserver<String>()
        val subject = AsyncSubject.create<String>()

        // when
        subject.onNext("A")
        subject.onNext("B")

        subject.subscribe(testObserver)

        subject.onNext("C")

        subject.onComplete() // 이게 호출되어야 값이 emit됨

        // then
        testObserver.assertValue("C")
        testObserver.assertComplete()
    }
}