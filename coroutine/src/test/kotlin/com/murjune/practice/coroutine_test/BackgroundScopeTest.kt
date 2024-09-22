package com.murjune.practice.coroutine_test

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * backgroundScope 는 runTest 에서 실행되는 코루틴이 종료되면 자동으로 취소 된다
 *
 * - 무한히 실행되는 작업의 경우 backgroundScope 를 사용하는 것이 좋다 (unCompletedJobException 방지)
 *
 * [주의점]
 * - backgroundScope 는 advanceUnilIdle 의 영향을 받지 않는다
 * - TestScope 에서 backgroundScope 작업을 대기하면 advanceUntilIdle 을 사용해도 대기하지 않는다
 *
 *
 * */
@OptIn(ExperimentalCoroutinesApi::class)
class BackgroundScopeTest {
    @Test
    fun `backgroundScope 는 runTest 에서 실행되는 코루틴이 종료되면 자동으로 취소 된다`() = runTest {
        val channel = Channel<Int>()
        backgroundScope.launch {
            var i = 0
            while (true) {
                channel.send(i++)
            }
        }
        repeat(100) {
            it shouldBe channel.receive()
        }
    }

    @Test
    fun `backgroundScope 에서 처리되지 않는 작업이 있어도, runTest 내 모든 작입이 끝나면 자동으로 취소 된다`() = runTest {
        backgroundScope.launch {
            while (true) {
                delay(10)
            }
        }
    }

    @Test
    fun `backgroundScope 는 advanceUnilIdle 의 영향을 받지 않는다`() = runTest {
        var number = 0
        backgroundScope.launch {
            while (true) {
                delay(10)
                number++
            }
        }
        advanceUntilIdle()
        currentTime shouldBe 0
        number shouldBe 0
    }

    @Test
    fun `runCurrent 를 사용하여 현재 시점(currenTime)에서 처리되지 않은 background 코루틴을 실행할 수 있다`() = runTest {
        var number = 0
        backgroundScope.launch {
            while (true) {
                delay(10)
                number++
            }
        }
        advanceTimeBy(20)
        runCurrent()
        number shouldBe 2
    }

    @Test
    fun `‼️ TestScope 에서 backgroundScope 작업을 대기하면 advanceUntilIdle 을 사용해도 대기하지 않는다`() =
        runTest {
            var count = 0
            val foregroundScope = this
            val backgroundJob =
                backgroundScope.launch { // advanceUntilIdle 은 foregroundScope 에 대해서만 동작한다
                    delay(200)
                    count++
                }
            foregroundScope.launch {
                delay(100)
                println("launch1")
                backgroundJob.join() // ‼️ advanceUntilIdle 을 사용해도 backgroundScope 의 작업은 대기하지 않는다
                delay(200) // 그래서 이 작업은 대기하지 않고 바로 실행되지 않는다
            }
            advanceUntilIdle()
            currentTime shouldBe 100
            count shouldBe 0
        }

    @Test
    fun `‼️ TestScope 에서 backgroundScope 작업을 대기하면 advanceUntilIdle 을 사용해도 대기하지 않는다 - sharedFlow 예시`() =
        runTest {
            // given
            val sharedFlow = MutableSharedFlow<Int>()
            val res = mutableListOf<Int>()
            // when
            launch {
                delay(10)
                sharedFlow.emit(1) // ‼️ MutableSharedFlow 는 buffer 가 없어 Suspend 되기 때문에 advanceUntilIdle 을 사용해도 대기하지 않는다
                delay(10)
                sharedFlow.emit(2)
                delay(10)
                sharedFlow.emit(3)
            }

            sharedFlow
                .onEach {
                    delay(30)
                    res.add(it)
                }
                .launchIn(backgroundScope)

            advanceUntilIdle()
            currentTime shouldBe 10
            res shouldBe emptyList()
        }
}