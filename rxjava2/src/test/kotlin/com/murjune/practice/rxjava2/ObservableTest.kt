package com.murjune.practice.rxjava2

import io.kotest.matchers.shouldBe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.TestObserver
import kotlin.test.Test

class ObservableTest {

    /**
     * [Disposable]은 RxJava에서 구독(subscription)을 나타내는 객체
     * 구독을 시작하면 [Observable]은 데이터를 방출하기 시작하는데,
     * 이걸 중단하려면 구독을 해제(dispose) 해야 해.
     *
     * 왜 해제해야함 > 메모리 누수 때문!!
     *
     * 예: 화면이 종료됐는데도 [Observable]이 계속 데이터를 방출하고 있다면, 리소스를 계속 사용하거나 UI가 죽은 화면에 반응하려고 해서 앱이 터질 수 있음.
     * */
    @Test
    fun testObservable() {
        // given
        // Observable 의 동작을 감시하고 테스트할 수 있는 객체
        val testObserver = TestObserver<String>()

        // when
        Observable.just("Apple", "Banana", "Cherry")
            .filter { it.startsWith("B") }
            .map { it.uppercase() }
            .subscribe(testObserver)

        // then
        val expect = "BANANA"
        testObserver.assertComplete()               // onComplete 호출되었는지
        testObserver.assertNoErrors()               // 에러 없이 수행됐는지
        testObserver.assertValue(expect)          // 하나의 값만 확인
        testObserver.assertValues(expect)         // 여러 개라면 assertValues("A", "B", ...)
        testObserver.values() shouldBe listOf("BANANA")   // 값을 직접 꺼내서 쓸 수도 있음
    }
}