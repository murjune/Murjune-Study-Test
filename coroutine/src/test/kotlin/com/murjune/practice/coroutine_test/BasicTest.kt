package com.murjune.practice.coroutine_test

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BasicTest {
    @Test
    fun `가상 시간 조절 - TestCoroutineScheduler`() {
        val testCoroutineScheduler = TestCoroutineScheduler()
        testCoroutineScheduler.advanceTimeBy(1000) // 1초 경과 (실제 시간은 반영 X)
        testCoroutineScheduler.currentTime shouldBe 1000
    }

    @Test
    fun `가상 시간 조절 - TestCoroutineScheduler, StandardTestDispatcher`() {
        val testCoroutineScheduler = TestCoroutineScheduler() // 사실 내부적으로 있어서 안 넣어도 됨
        val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
        // testDIspatcher 에 의해 관리됨 - 따라서, 실제로 시간이 경과하지 않음
        val testScope = CoroutineScope(testDispatcher)
        // given
        var result = 0
        // when
        testScope.launch {
            delay(10_000) // 10초 대기 (실제 시간은 반영 X)
            result = 1
        }
        // then
        result shouldBe 0
        testCoroutineScheduler.advanceTimeBy(10_100) // 10초 경과 (실제 시간은 반영 X)
        result shouldBe 1
        testCoroutineScheduler.currentTime shouldBe 10_100
    }

    @Test
    fun `advanceUntilIdle - 모든 디스패처와 연결된 작업이 모두 완료될 때까지 가상 시간을 흐르게함`() {
        val testDispatcher = StandardTestDispatcher()
        // testDIspatcher 에 의해 관리됨 - 따라서, 실제로 시간이 경과하지 않음
        val testScope = CoroutineScope(testDispatcher)
        // given
        var result = 0
        // when
        testScope.launch {
            delay(10_000) // 10초 대기 (실제 시간은 반영 X)
            result = 1
        }
        // then
        testDispatcher.scheduler.advanceUntilIdle() // 모든 디스패처와 연결된 작업이 모두 완료될 때까지 가상 시간을 흐르게함
        result shouldBe 1
    }

    @Test
    fun `TestScope - 가상 시간 테스트`() {
        val testScope = TestScope() // 요녀석을 쓰면 위와 같이 디스패처를 만들 필요가 없음
        // given
        var result = 0
        // when
        testScope.launch {
            delay(10_000) // 10초 대기 (실제 시간은 반영 X)
            result = 1
        }
        // then
        testScope.advanceUntilIdle() // 모든 디스패처와 연결된 작업이 모두 완료될 때까지 가상 시간을 흐르게함
        result shouldBe 1
    }

    @Test
    fun `runTest - 얘가 알아서 다 해줌`() = runTest {
        // given
        var result = 0
        // when
        delay(10_000)
        result = 1
        // then
        result shouldBe 1
    }

    @Test
    fun `runTest + advanceUntilIdle`() = runTest {
        // given
        var result = 0
        // when
        launch {
            delay(10_000) // 10초 대기 (실제 시간은 반영 X)
            result = 1
        }
        // then
        advanceUntilIdle() // 모든 디스패처와 연결된 작업이 모두 완료될 때까지 가상 시간을 흐르게함
        result shouldBe 1
    }
}
