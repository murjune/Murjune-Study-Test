package com.murjune.practice.coroutine_test

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineBasicOfBasicTest {
    @Test
    fun `runTest 는 가상 시간에서 돌아간다`() = runTest {
        val time = measureTime {
            delay(4000)
        }
        time shouldBeLessThan 10.milliseconds
    }

    @Test
    fun `measureTimeMillis 는 실제 시간을 측정한다`() = runTest {
        val time = measureTimeMillis {
            delay(1000_000)
        }
        println(time) // output: 1ms
    }

    @Test
    fun `테스트 가상 환경에서 시간 경과 테스트2`() = runTest {
        var count = 0
        val job = launch {
            delay(3_000)
            count++
        }
        // 가상 시간 3초 경과
        advanceTimeBy(3_000)
        currentTime shouldBe 3_000
        job.isActive.shouldBeTrue()
        count shouldBe 0
        // 남은 job 실행
        runCurrent()
        count shouldBe 1
        job.isCompleted.shouldBeTrue()
    }

    @Test
    fun `테스트 가상 환경에서 시간 경과 테스트`() = runTest {
        launch(CoroutineName("작업 A")) {
            println("작업 A start -- currentTime:${currentTime}ms")
            delay(2_999)
            println("작업 A Done -- currentTime:${currentTime}ms")
        }
        launch(CoroutineName("작업 B")) {
            println("작업 B start -- currentTime:${currentTime}ms")
            delay(3000)
            println("작업 B Done -- currentTime:${currentTime}ms")
        }
        yield()
        println("advanceTimeBy(3000) 호출")
        advanceTimeBy(3_000)
        println("runCurrent() 호출 -- currentTime:${currentTime}ms")
        runCurrent()
    }

    @Test
    fun `advanceUntilIdle`() = runTest {
        var count = 0
        val job1 = launch(CoroutineName("child1")) {
            delay(3000)
            count++
        }
        val job2 = launch(CoroutineName("child2")) {
            delay(5000)
            count++
        }
        advanceUntilIdle()
        currentTime shouldBe 5000
        count shouldBe 2
        job1.isCompleted.shouldBeTrue()
        job2.isCompleted.shouldBeTrue()
    }
}
