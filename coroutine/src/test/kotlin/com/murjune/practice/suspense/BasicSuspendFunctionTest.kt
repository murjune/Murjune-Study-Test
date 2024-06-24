package com.murjune.practice.suspense

import com.murjune.practice.utils.shouldBeActive
import com.murjune.practice.utils.shouldBeCompleted
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.coroutineContext
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BasicSuspendFunctionTest {

    // Suspend function 은 코루틴이 아니다
    // Suspend function 은 코루틴 내에서 돌아가는 코드 블럭에 불과하다
    @Test
    fun `Suspend 함수는 코루틴이 아니다`() = runTest {
        val job = launch {
            suspendFuncA()
            suspendFuncB()
        }
        advanceTimeBy(300)
        job.shouldBeActive()
        advanceTimeBy(300)
        job.shouldBeCompleted()
        // output: Hello Odooong
    }

    @Test
    fun `Suspend 함수는 코루틴이 아니다 - 위 테스트 함수와 완전히 동일`() = runTest {
        val job = launch {
            delay(300)
            print("Hello ")
            delay(200)
            print("Odooong")
        }
        advanceTimeBy(300)
        job.shouldBeActive()
        advanceTimeBy(300)
        job.shouldBeCompleted()
        // output: Hello Odooong
    }

    // 비동기 실행
    // suspend function 안에 CoroutineScope 를 만들어서 비동기 실행하는 것이 좋을까?
    // 성능적으로는 좋을 수 있으나, 잘못 설계한 것이다.
    // 동료 개발자는 suspend 함수를 호출할 때 내부에서 CoroutineScope 를 활용할 것이라 생각하지 않는다..
    @Test
    fun `suspend 함수 내에 코루틴 스코프를 열어 자식 코루틴 생성 - 동시성`() = runTest {
        val job = launch {
            suspendFuncAWithCoroutineScope()
            suspendFuncBWithCoroutineScope()
        }
        advanceTimeBy(400)
        job.shouldBeCompleted()
        // Output: Odooong Hello
    }

    // 참고)
    @Test
    fun `다른 코루틴 디스패처를 사용하면 구조화된 동시성이 깨져, 독립된 코루틴이 된다`() = runTest {
        val job = launch {
            // job 은 아래 suspend 함수에서 생성된 코루틴을 관리하지 못한다
            suspendFuncWithAnotherCoroutineScope()
        }
        advanceTimeBy(100)
        job.shouldBeCompleted()
        // output: x
        // Hellow Odooong 이 호출되지 않음
    }

    // 이렇게 suspend 함수 호출부에서 병렬 처리해줄 수도 있다.
    @Test
    fun `호출부에서 자식 코루틴 생성`() = runTest {
        val parentJob = launch {
            launch {
                suspendFuncA()
            }
            launch {
                suspendFuncB()
            }
        }
        advanceTimeBy(400)
        parentJob.shouldBeCompleted()
        // output: Odooong Hello
    }

    // suspend 함수 내부에서 병렬 처리를 하고 무언가 반환 값을 받아야 할 때
    // coroutineScope 와 async 를 사용할 수 있다.
    @Test
    fun `우테코 선릉 캠퍼스 크루들 불러오기 -  병렬 처리`() = runTest {
        val crews = fetchWootecoCrews()
        currentTime shouldBeGreaterThan 100
        currentTime shouldBeLessThan 400
        crews shouldContainExactlyInAnyOrder listOf("오둥이", "꼬상", "하디", "팡태", "악어", "케이엠", "토다리", "제이드")
    }

    @Test
    fun `supervisorScope 를 활용하여 Error 전파 방지`() = runTest {
        val coaches = fetchWootecoCoaches()
        currentTime shouldBeLessThan 400
        coaches shouldContainExactlyInAnyOrder listOf("제이슨", "레아", "제이슨", "준", "크론")
    }

    private suspend fun suspendFuncA() {
        delay(300)
        println("Hello")
    }

    private suspend fun suspendFuncB() {
        delay(200)
        println("Odooong")
    }

    private suspend fun suspendFuncAWithCoroutineScope() {
        CoroutineScope(coroutineContext).launch {
            delay(300)
            print(" Hello ")
        }
    }

    private suspend fun suspendFuncBWithCoroutineScope() {
        CoroutineScope(coroutineContext).launch {
            delay(200)
            print(" Odooong ")
        }
    }

    private suspend fun suspendFuncWithAnotherCoroutineScope() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(200)
            println("Hellow Odooong")
        }
    }

    private suspend fun fetchWootecoAndroidCrews(): List<String> {
        delay(300)
        return listOf("오둥이", "꼬상", "하디", "팡태", "악어", "케이엠")
    }

    private suspend fun fetchWootecoFrontendCrews(): List<String> {
        delay(200)
        return listOf("토다리", "제이드")
    }

    private suspend fun fetchAndroidCoaches(): List<String> {
        delay(300)
        return listOf("제이슨", "레아", "제이슨")
    }

    private suspend fun fetchFrontCoaches(): List<String> {
        delay(300)
        return listOf("준", "크론")
    }

    private suspend fun fetchBackCoaches(): List<String> {
        delay(150)
        throw NoSuchElementException("제가 백엔드 코치님들은 모릅니다..")
    }

    private suspend fun fetchWootecoCrews() = coroutineScope {
        val androidJob = async { fetchWootecoAndroidCrews() }
        val frontJob = async { fetchWootecoFrontendCrews() }
        androidJob.await() + frontJob.await()
    }

    private suspend fun fetchErrorWootecoCoaches() = coroutineScope {
        val androidJob = async { fetchAndroidCoaches() }
        val frontJob = async { fetchFrontCoaches() }
        val backendJob = async { fetchBackCoaches() }
        androidJob.await() + frontJob.await() + backendJob.await()
    }

    // 만약, coroutineScope 내에서 발생하는 에러가 부모에게 전파시키고 싶지 않다면
    // supervisorScope 를 사용하면 된다.
    // 참고로 async 내부에서 에러 발생 시 전파되는 예외 + await() 에서 발생하는 예외가 모두 처리 해줘야 한다.
    private suspend fun fetchWootecoCoaches() = supervisorScope {
        val androidJob = async { fetchAndroidCoaches() }
        val frontJob = async { fetchFrontCoaches() }
        val backendJob = async { fetchBackCoaches() }
        // result
        val backendResult = runCatching { backendJob.await() }
        androidJob.await() + frontJob.await() + backendResult.getOrDefault(emptyList())
    }
}
