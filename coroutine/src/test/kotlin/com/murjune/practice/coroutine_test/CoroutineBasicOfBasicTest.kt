package com.murjune.practice.coroutine_test

import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
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
    fun `테스트 가상 환경에서 시간 경과 테스트`() = runTest {
        launch {
            delay(3_000)
        }
        advanceTimeBy(3_000)
        currentTime shouldBe 3_000
    }

    @Test
    fun `advanceUntilIdle`() {
        runTest {
            launch { delay(300) }
            launch { delay(500) }
            advanceUntilIdle()
            currentTime shouldBe 500
        }
    }
}
