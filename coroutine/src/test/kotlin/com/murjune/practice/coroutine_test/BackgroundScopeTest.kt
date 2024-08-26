package com.murjune.practice.coroutine_test

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

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
    fun `backgroundScope 에서 처리되지 않는 작업이 있을 때 처리된 후 종료되낟`() = runTest {
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
    fun `backgroundScope 에서 처리되지 않는 작업이 있을 때, 그 시점(currentTime)에 해당하는 작업만 수행하고 종료된다`() = runTest {
        val sharedFlow = MutableSharedFlow<Int>()
        launch {
            repeat(3) {
                delay(10)
                println("emit: $it - $currentTime")
                sharedFlow.emit(it)
            }
        }.invokeOnCompletion {
            println("finit produce")
        }

        backgroundScope.launch {
            sharedFlow.onEach {
                println("onEach >> $it")
            }.buffer(3).collect {
                delay(30)
                it shouldBe 0
                currentTime shouldBe 30
            }
        }
    }
}