package com.murjune.practice.channel

import com.murjune.practice.utils.runErrorTest
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ChannelAsFlowTest {
    @Test
    fun `consumeAsFlow 로 변환된 Flow 는 다수의 collector 가 존재할 수 있다`() = runTest {
        // given
        val channel = Channel<Int>()
        val flow = channel.receiveAsFlow()
        launch {
            repeat(10) {
                channel.send(it)
            }
        }

        flow.onEach {
            println("Collector1 - collect $it")
        }.launchIn(this)

        flow.onEach {
            println("Collecto2 - collect $it")
        }.launchIn(this)

        advanceUntilIdle()
        coroutineContext.cancelChildren()
    }

    @Test
    fun `consumeAsFlow 로 변환된 Flow 는 단 하나의 collector 만 존재할 수 있다`() = runErrorTest<IllegalStateException> {
        // given
        val channel = Channel<Int>()
        val flow = channel.consumeAsFlow()
        launch {
            repeat(10) {
                channel.send(it)
            }
        }

        flow.onEach {
            println("collect $it")
        }.launchIn(this)

        flow.onEach {
            // throw IllegalStateException
            println("Collecto2 - collect $it")
        }.launchIn(this)
    }
}