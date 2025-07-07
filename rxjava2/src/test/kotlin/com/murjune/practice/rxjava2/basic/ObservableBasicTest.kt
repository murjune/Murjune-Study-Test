package com.murjune.practice.rxjava2.basic

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import java.util.concurrent.TimeUnit

/**
 * Observable의 기본 개념과 생성 방법을 학습한다.
 *
 * 학습 목표:
 * 1. Observable의 기본 생성 방법들 (just, fromArray, range 등)
 * 2. 구독(subscribe)과 해제(dispose)의 중요성
 * 3. 데이터 스트림의 기본 동작 방식
 */
class ObservableBasicTest : BehaviorSpec({

    Given("Observable 기본 활용법") {
        lateinit var testObserver: TestObserver<Any>

        beforeTest {
            testObserver = TestObserver<Any>()
        }

        When("just()로 단일 값 생성") {
            Then("값이 정상적으로 방출") {
                Observable.just("Hello RxJava")
                    .subscribe(testObserver)

                testObserver.assertComplete()
                testObserver.assertNoErrors()
                testObserver.assertValue("Hello RxJava")
            }
        }

        When("just()로 여러 값 생성") {
            Then("모든 값이 순서대로 방출") {
                Observable.just("Apple", "Banana", "Cherry")
                    .subscribe(testObserver)

                testObserver.assertComplete()
                testObserver.assertNoErrors()
                testObserver.assertValues("Apple", "Banana", "Cherry")
            }
        }

        When("fromArray()로 배열 생성") {
            val numbers = arrayOf(1, 2, 3, 4, 5)

            Then("배열의 모든 원소가 방출되어야 한다") {
                Observable.fromArray(*numbers)
                    .subscribe(testObserver)

                testObserver.assertComplete()
                testObserver.assertNoErrors()
                testObserver.assertValues(1, 2, 3, 4, 5)
            }
        }

        When("range()로 범위 생성") {
            Then("지정된 범위의 숫자가 방출되어야 한다") {
                Observable.range(1, 5)
                    .subscribe(testObserver)

                testObserver.assertComplete()
                testObserver.assertNoErrors()
                testObserver.assertValues(1, 2, 3, 4, 5)
            }
        }

        When("empty()로 빈 Observable 생성") {
            Then("값 없이 완료되어야 한다") {
                Observable.empty<String>()
                    .subscribe(testObserver)

                testObserver.assertComplete()
                testObserver.assertNoErrors()
                testObserver.assertValueCount(0)
            }
        }
    }

    Given("Observable 은 Cold Stream 이다") {
        lateinit var testObserver1: TestObserver<Any>
        lateinit var testObserver2: TestObserver<Any>

        beforeTest {
            testObserver1 = TestObserver<Any>()
            testObserver2 = TestObserver<Any>()
        }

        When("Observable을 구독할 때마다 새로운 데이터 스트림 생성") {
            val observable = Observable.just(1, 2, 3)

            Then("각 구독마다 새로운 데이터 방출") {
                observable.subscribe(testObserver1)
                observable.subscribe(testObserver2)

                testObserver1.assertValues(1, 2, 3)
                testObserver2.assertValues(1, 2, 3)
            }
        }

        When("Observable을 구독을 안하면 데이터가 방출되지 않음") {
            val observable = Observable.just(1, 2, 3)

            Then("doOnNext가 호출되지 않아야 한다") {
                observable.doOnNext { error("Never should be called") }
            }
        }
    }

    Given("Observable 예외 처리") {
        lateinit var testObserver: TestObserver<Any>

        beforeTest {
            testObserver = TestObserver<Any>()
        }

        When("Observable에서 에러 발생") {
            val errorMessage = "테스트 에러"
            Then("onError 가 호출되어야 한다") {
                Observable.error<String>(RuntimeException(errorMessage))
                    .subscribe(testObserver)

                testObserver.assertError(RuntimeException::class.java)
                testObserver.assertNotComplete()
                testObserver.assertValueCount(0)
            }
        }

        When("데이터 방출 중 에러 발생") {
            Then("에러 발생 전까지의 데이터는 방출되어야 한다") {
                Observable.fromArray(1, 2, 3, 4, 5)
                    .map {
                        if (it == 3) throw RuntimeException("3에서 에러!")
                        it * 2
                    }
                    .subscribe(testObserver)

                testObserver.assertError(RuntimeException::class.java)
                testObserver.assertValues(2, 4)  // 1*2, 2*2만 방출됨
                testObserver.assertNotComplete()
            }
        }
    }

    Given("Disposable을 통한 구독 해제 관리") {

        lateinit var testScheduler: TestScheduler

        beforeTest {
            testScheduler = TestScheduler()
        }


        When("구독 후 즉시 해제") {
            var count = 0

            Then("데이터 방출이 중단되어야 한다") {

                val disposable = Observable
                    .interval(1, TimeUnit.MILLISECONDS, testScheduler)
                    .doOnNext { count++ }
                    .subscribe()

                // 구독 후 즉시 dispose
                disposable.dispose()

                // 시간이 흘러도 doOnNext는 호출되지 않아야 함
                testScheduler.advanceTimeBy(2, TimeUnit.MILLISECONDS)

                count shouldBe 0
            }
        }

        When("구독 해제 상태 확인") {
            Then("dispose 후 isDisposed가 true여야 한다") {
                val disposable = Observable
                    .just(1, 2, 3)
                    .delay(1, TimeUnit.SECONDS, testScheduler)
                    .subscribe()

                disposable.isDisposed.shouldBeFalse()

                disposable.dispose()

                disposable.isDisposed.shouldBeTrue()
            }
        }
    }
})