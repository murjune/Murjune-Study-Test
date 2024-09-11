package com.murjune.practice.exception.supervisor

import com.murjune.practice.suspense.fetchWootecoBackendCrews
import com.murjune.practice.suspense.fetchWootecoFrontendCrews
import com.murjune.practice.utils.runErrorTest
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineExceptionPropagateLimitTest {
    // Job 을 사용한 예외 전파 제한
    // 코루틴은 자신의 부모 코루틴으로만 예외를 전파한다.
    // 따라서, 부모 코루틴과의 계층 구조를 끊어주는 Job 을 사용하면 예외를 전파하지 않는다.
    // 참고) launch 함수에 새로운 Job 을 전달하면 새로운 Job 을 부모 Job 으로 사용한다.
    // 따라서, parent1, parent2 둘다 실행되는 것을 볼 수 있다
    @Test
    fun `Job 을 사용한 예외 전파 제한 - Job() 빌더`() = runErrorTest<IllegalStateException> {
        launch(Job()) {
            println("${coroutineContext[Job]?.parent}")
            delay(30)
            println("error 처리")
            error("error")
        }
        delay(40)
        println("시스템이 강제 종료되지 않고 정상 종료됨")
    }

    //    CoroutineExceptionHandler { _, throwable ->
//        if (throwable is UnknownHostException) {
//            logError("Uncaught exception: ${throwable.message}", throwable = null)
//        } else {
//            logError("Uncaught exception", throwable)
//        }
//        when (throwable) {
//            is UnknownHostException, is ConnectException -> {
//                context.showToast(R.string.outln_toast_check_network)
//            }
//            else -> {
//                context.showToast(R.string.outln_toast_something_wrong)
//            }
//        }
//    }
    @Test
    fun `Job 을 사용한 예외 전파 제한 - new CoroutineScope`() = runErrorTest<IllegalStateException> {
        val j = CoroutineScope(EmptyCoroutineContext).launch {
            delay(100)
            println("error 처리")
            error("error")
        }
        j.join()
        println("시스템이 강제 종료되지 않고 정상 종료됨")
        delay(200)
    }
    // 새로운 Job 을 추가해줌으로써 부모-자식 관계를 끊어주면, 예외 전파는 막을 수 있다.
    // 그러나, 부모 --> 자식으로 전파되는 cancel 도 막게된다.
    // 코루틴을 사용하는 이유 중 하나가 구조화된 동시성을 통해 작업을 안전하게 취소할 수 있는 것인데,
    // 취소를 원하는데로 할 수 없게 된다면 코루틴을 사용하는 이유가 없어진다.

    // *** 부모 잡 <-> 자식 잡은 서로 참조가 가능하다
    // 따라서,잡을 참조할 수 있는 부모-자식 관계를 유지하기에 코루틴 스코프 내에서 예외처리 및 취소가 가능한 것이다.

    // 작업을 안전하게 취소해야하는 이유 : 리소스 누수, 불필요한 작업, 불필요한 메모리 사용 등을 막기 위함

    // 예시) 우테코 크루들의 이름을 가져오는 작업
    // 각 파트의 크루들의 이름을 가져오는 작업을 병렬로 진행하고 있다.
    // 이때 사용자가 다운로드 취소를 하면 모든 자식 코루틴을 취소해야하는데,
    // 새로운 Job을 추가해 부모-자식 관계를 끊어주면 이러한 작업을 할 수 없다.

    // 이미지3 다운로드 작업은 더이상 root 의 자식이 아니기 때문에 cancel 이 되지 않는다.
    // 따라서, 취소해도 이미지 3 다운로드 작업은 계속 진행된다.

    // 뿐만 아니라, 새로운 Job 이 생겨 root 는 job이 끝날 때까지 기다리지 않는다.
    @Test
    fun `test`() = runTest {
        val wootecoCrewJob = launch(CoroutineName("우테코 6기 크루들 이름 불러오기")) {
            val androidCrews = async(Job()) {
                println("coroutineContext[Job]?.parent : ${coroutineContext[Job]?.parent}")
                error("안드로이드 크루 에러 발생")
            }
            val frontendCrews = async { fetchWootecoFrontendCrews() }
            val backendCrews = async { fetchWootecoBackendCrews() }
            val result = frontendCrews.await() + backendCrews.await()
            println("우테코 크루들 : $result")
        }
        delay(500)
    }
}