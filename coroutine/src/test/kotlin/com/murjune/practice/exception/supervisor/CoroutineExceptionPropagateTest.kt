package com.murjune.practice.exception.supervisor

import com.murjune.practice.utils.launchWithName
import com.murjune.practice.utils.log
import com.murjune.practice.utils.runErrorTest
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.concurrent.thread
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineExceptionPropagateTest {
    @Test
    fun `코루틴 예외 발생 - basic`() = runErrorTest<IllegalStateException> {
        val p = launch(CoroutineName("parent")) {
            log("parent - 시작")
            launch(CoroutineName("child")) {
                log("child - 시작")
                delay(100)
                log("child - error 발생 👻")
                error("error") // 1. 예외 발생
            }
            launch(CoroutineName("child2")) {
                log("child2 - 시작")
                delay(300)
                log("child2 - 종료")
            }
            log("parent - 끝")
        }
        p.join()
    }

    @Test
    fun `test`() {
        runTest {
            val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
                println(throwable.message)
            }
            launch(handler) {
                supervisorScope {
                    launch {
                        delay(100)
                        error("supervisorScope")
                    }
                }
            }
            val job = SupervisorJob(coroutineContext.job)
            CoroutineScope(job + handler).launch {
                launch {
                    delay(100)
                    error("superVisorJob")
                }
            }
            job.complete()
        }
    }

    // val t = try {
//                thread(name = "thread-1") {
//                    log("start")
//                    Thread.sleep(100)
//                    error("error 발생 👻")
//                }
//            } finally {
//            }
    @Test
    fun `test - thread 와 비교해보자`() {
        thread(name = "parent") {
            log("start")
            val t = thread(name = "child-1") {
                log("start")
                Thread.sleep(100)
                error("error 발생 👻")
            }
            val t2 = thread(name = "child-2") {
                log("start")
                Thread.sleep(300)
                log("finish")
            }
            t.join()
            t2.join()
            log("finish")
        }
    }

    @Test
    fun `코루틴의 예외 전파 방식`() = runErrorTest<IllegalStateException> {
        launchWithName("root") {
            launchWithName("parent1") {
                launchWithName("child") {
                    delay(10)
                    log("child - error 발생 👻")
                    error("error") // 1. 예외 발생
                }
                launchWithName("child2") {
                    delay(20)
                }
            }
            launchWithName("parent2") { // 4. 취소
                launchWithName("child3") {
                    delay(20)
                }
                launchWithName("child4") {
                    delay(20)
                }
                // child 종료 -> parent1 로 예외 전파 -> child2 취소 -> parent1 취소
                // root로 예외 전파 -> parent2 로 예외 전파 -> child3 취소 -> child4 취소 -> parent2 취소
                // root 취소
            }
        }
    }

    @Test
    fun `코루틴의 예외 전파 방식2 - Postorder Traversal`() = runErrorTest<IllegalStateException> {
        // 3. 예외 전파
        launchWithName("root") {
            launchWithName("parent1") {
                launchWithName("child") {
                    delay(10)
                }
                launchWithName("child2") {
                    delay(20)
                }
                delay(5)
                log("parent1 - error 발생 👻")
                error("error")
            }
            launchWithName("parent2") { // 4. 취소
                launchWithName("child3") {
                    delay(20)
                }
                launchWithName("child4") {
                    delay(20)
                }
            }
        }
        // child 종료 -> parent1 로 예외 전파 -> child2 취소 -> parent1 취소
        // root로 예외 전파 -> parent2 로 예외 전파 -> child3 취소 -> child4 취소 -> parent2 취소
        // root 취소
    }

    @Test
    fun `구조화를 깨는 행위는 예외를 전파를 제한하는거지 예외 처리를 하는 것은 아니다`() = runErrorTest<IllegalStateException> {
        val parentJob = Job()
        val deferred = async(parentJob) {
            log("async")
            error("error")
        }
        parentJob.complete()
        parentJob.join()
        deferred.await()
    }


    @Test
    fun `잘못된 예외 처리 방식 - try catch`() = runErrorTest<IllegalStateException> {
        try {
            launchWithName("parent2") {
                error("error")
            }
        } catch (e: Exception) {
            log("예외 처리") // never executed
        }
    }

    // https://github.com/Kotlin/kotlinx.coroutines/issues/3374
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testNotPropagatingExceptions() = runTest {
        val handler = CoroutineExceptionHandler { _, exception ->
            exception.message shouldBe "error"
        }
        CoroutineScope(NonCancellable + handler).launch {
            error("error")
        }
        advanceUntilIdle()
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `some_test`() {
        val testDispatcher = StandardTestDispatcher()
        val scope = CoroutineScope(testDispatcher)
        val throwError = suspend { throw Exception() }
        var caught: Throwable? = null
        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            caught = throwable
        }
        scope.launch(handler) {
            throwError()
        }
        testDispatcher.testCoroutineScheduler.runCurrent()
        caught.shouldNotBeNull()
    }
}

/**
개발자에게 예외 처리는 반드시 필요한 작업이다.
예외처리를 적절히 해주지 않으면, 앱이 개발자가 의도하지 않는 방향으로 동작할 수도 있고
심한 경우 앱이 강제 종료될 수도 있다.

이는 서비스의 품질을 떨어뜨리는 요인이 될 수 있기 때문에, 개발자는 예외처리에 반드시 신경을 써야한다.

코루틴은 구조화된 동시성 원칙을 따라 비동기 작업을 구조화하여 보다 안정적이고 예측 가능하게 코드를 작성할 수 있게 해준다.
따라서, 코틀린을 사용하는 개발자라면 File I/O, 네트워크 통신, DB 작업을 할 때 코루틴을 활용한  비동기 처리를
많이 사용할 것이다. 해당 작업들은 대부분 예상치 못한 예외들이 발생할 수 있기 때문에
코루틴 예외 처리에 대해 반드시 잘 숙지하고 있어야 한다.

즉, 비동기 작업인 코루틴을 부모-자식 관계로 구조화함으로써 코루틴을 안정적으로 관리할 수 있다.
따라서, 예외 처리도 부모-자식 관계로 구조화하여 안정적으로 관리할 수 있다.

이번 포스팅에서는 코루틴이 예외를 전파하는 방식과 예외 전파를 제한하는 방법에 대해 알아보겠다.
다음 포스팅에서는 예외를 처리하는 방법과 예외 처리 심화 내용에 대해 알아보겠다.

 * */

private suspend fun ex2() = coroutineScope {
    val parentJob = launchWithName("parent1") {
        // 새로운 Job 을 생성하여 부모 Job 에 추가
        launchWithName("parent1-child", Job()) {
            error("error")
        }
        delay(100)
        log("parent1 실행중")
    }
    launchWithName("parent2", parentJob) {
        delay(100L)
        log("parent2 실행중")
    }
    delay(1000)
}

fun main() {
    thread(name = "parent") {
        log("start")
        val t = thread(name = "child-1") {
            log("start")
            Thread.sleep(100)
            log("finish")
        }
        val t2 = thread(name = "child-2") {
            log("start")
            Thread.sleep(300)
            log("finish")
        }
        t.join()
        t2.join()
        log("finish")
    }
}
