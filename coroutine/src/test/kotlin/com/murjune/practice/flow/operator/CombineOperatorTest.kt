package com.murjune.practice.flow.operator

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test


@OptIn(ExperimentalCoroutinesApi::class)
class CombineOperatorTest {

    @Test
    fun `onStart 연산자는 collect 를 시작할 때 호출된다`() = runTest {
        // given
        val mutableStateFlow = MutableStateFlow(1)
        var callOnStartTimes = 0L
        val flow = mutableStateFlow
            .onStart {
                callOnStartTimes = currentTime
            }

        delay(100)
        // 100ms -collecrtor 1 - onStart 호출
        flow.launchIn(backgroundScope)
        advanceUntilIdle()
        runCurrent()
        callOnStartTimes shouldBe 100L

        delay(100)
        // 200ms - collecrtor 2 - onStart 호출
        flow.launchIn(backgroundScope)
        advanceUntilIdle()
        runCurrent()
        callOnStartTimes shouldBe 200L

        delay(100)
        // 200ms - collecrtor 3 - onStart 호출
        flow.launchIn(backgroundScope)
        advanceUntilIdle()
        runCurrent()
        callOnStartTimes shouldBe 300
    }

    @Test
    fun `Combine 연산자는 Upstream Flow 들이 값을 방출할 때마다 값을 방출한다`() = runTest {
        // given
        val flow1 = flowOf(1, 2, 3).onEach { delay(20) }
        val flow2 = flowOf("a", "b", "c").onEach { delay(31) }
        val res = mutableListOf<String>()
        // when
        // 10ms   20ms   30ms   40ms   50ms   60ms   7ms   8ms   9ms
        //          1            2             3
        //                 a                     b                 c
        combine(flow1, flow2) { a, b ->
            "$a$b"
        }.onEach {
            res.add(it)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        val expected = listOf("1a", "2a", "3a", "3b", "3c")
        res shouldBe expected
    }

    @Test
    fun `대기 중인 element 가 있을 때, 새로운 element 가 방출되면 새로운 element 를 방출한다 - conflate`() = runTest {
        // given
        val flow1 = flowOf(1, 2, 3).onEach { delay(7) }
        val flow2 = flowOf("a", "b", "c").onEach { delay(15) }
        val res = mutableListOf<String>()
        // when
        // 7ms   14ms   21ms   28ms   35ms   42ms   49ms
        //  1(🔥유실) 2       3
        //             a            b              c
        combine(flow1, flow2) { a, b ->
            "$a$b"
        }.onEach {
            res.add(it)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        val expected = listOf("2a", "3a", "3b", "3c")
        res shouldBe expected
    }

    @Test
    fun `‼️ 모든 Uptream Flow 들이 첫 번째 쌍을 방출해야 combine 이 값을 방출한다`() = runTest {
        // given
        val flow1 = emptyFlow<Int>()
        val flow2 = flowOf(1, 2, 3)
        val res = mutableListOf<Int>()
        // when
        combine(flow1, flow2) { a, b ->
            res.add(a)
            res.add(b)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        res.shouldBeEmpty()
    }

    // 주의해야할 점은 SharedFlow 는 onStart 연산자를 사용할 수 없다. flow/sharedflow/SharedFlowTest.kt 를 참고하자.
    @Test
    fun `✅ Uptream Flow 중 하나라도 값이 변경될때마다 값을 방출하고 싶다면, onStart 연산자를 사용하자`() = runTest {
        // given
        val flow1 = emptyFlow<Int>()
        val flow2 = flowOf(1, 2, 3)
        val res = mutableListOf<Int>()
        // when
        combine(flow1.onStart { emit(0) }, flow2) { a, b ->
            res.add(b + a)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        res shouldBe listOf(1, 2, 3)
    }
}