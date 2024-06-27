package com.murjune.practice.suspense

import com.murjune.practice.utils.shouldBeCompleted
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.coroutineContext
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BasicSuspendFunctionTest {

    @Test
    fun `suspend 함수는 코루틴이 아니다`() = runTest {
        val job = launch {
            suspendFuncA()
            suspendFuncB()
        }
        advanceUntilIdle()
        currentTime shouldBe 500
        job.shouldBeCompleted()
        // output: Hello Odooong
    }

    @Test
    fun `위 테스트 함수와 완전히 동일`() = runTest {
        val job = launch {
            delay(300)
            print("Hello ")
            delay(200)
            print("Odooong")
        }
        advanceUntilIdle()
        currentTime shouldBe 500
        job.shouldBeCompleted()
        // output: Hello Odooong
    }

    // 비동기 실행
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

    @Test
    fun `suspend 함수 내에 코루틴 스코프를 열어 자식 코루틴 생성 - 동시성`() = runTest {
        suspendFuncAWithCoroutineScope()
        suspendFuncBWithCoroutineScope()
        advanceUntilIdle()
        currentTime shouldBe 300
        // Output: Odooong Hello
    }

    @Test
    fun `suspend 함수 분리 - 동시성`() = runTest {
        mergedSuspendFunc()
        currentTime shouldBe 300
        // Output: Odooong Hello
    }

    @Test
    fun `다른 코루틴 디스패처를 사용하면 구조화된 동시성이 깨져, 독립된 코루틴이 된다`() = runTest {
        launch {
            suspendFuncWithAnotherCoroutineScope()
        }
        advanceUntilIdle()
        currentTime shouldBe 0
        // output: x
        // Hellow Odooong 이 호출되지 않음
    }

    @Test
    fun `우테코 선릉 캠퍼스 크루들 불러오기 -  동시성`() = runTest {
        val crews = fetchWootecoCrews()
        currentTime shouldBe 300
        crews shouldContainExactlyInAnyOrder listOf("오둥이", "꼬상", "하디", "팡태", "악어", "케이엠", "토다리", "제이드")
    }

    @Test
    fun `하나라도 예외가 발생하면, 모든 작업이 취소된다`() = runTest {
        shouldThrow<NoSuchElementException> {
            fetchErrorWootecoCoaches()
        }
        currentTime shouldBe 70
    }

    @Test
    fun `supervisorScope 를 활용하여 Error 전파 방지`() = runTest {
        val coaches = fetchWootecoCoaches()
        currentTime shouldBe 150
        coaches shouldContainExactlyInAnyOrder listOf("제이슨", "레아", "제임스", "준", "크론")
    }

    private suspend fun suspendFuncA() {
        delay(300)
        println("Hello")
    }

    private suspend fun suspendFuncB() {
        delay(200)
        println("Odooong")
    }

    private suspend fun mergedSuspendFunc(): Unit = coroutineScope {
        launch {
            suspendFuncA()
        }
        launch {
            suspendFuncB()
        }
    }

    private suspend fun suspendFuncAWithCoroutineScope() {
        CoroutineScope(coroutineContext + CoroutineName("ChildA")).launch {
            delay(300)
            print(" Hello ")
        }
    }

    private suspend fun suspendFuncBWithCoroutineScope() {
        CoroutineScope(coroutineContext + CoroutineName("ChildB")).launch {
            delay(200)
            print(" Odooong ")
        }
    }

    private fun suspendFuncWithAnotherCoroutineScope() {
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
        delay(50)
        return listOf("제이슨", "레아", "제임스")
    }

    private suspend fun fetchFrontCoaches(): List<String> {
        delay(150)
        return listOf("준", "크론")
    }

    private suspend fun fetchBackCoaches(): List<String> {
        delay(70)
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

    private suspend fun fetchWootecoCoaches() = supervisorScope {
        val androidJob = async { fetchAndroidCoaches() }
        val frontJob = async { fetchFrontCoaches() }
        val backendJob = async { fetchBackCoaches() }
        // result
        val backendResult = runCatching { backendJob.await() }
        androidJob.await() + frontJob.await() + backendResult.getOrDefault(emptyList())
    }
}
