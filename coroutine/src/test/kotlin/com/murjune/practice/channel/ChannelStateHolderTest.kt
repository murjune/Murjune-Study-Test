package com.murjune.practice.channel

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChannelStateHolderTest {
    @Test
    fun `CONFLATED - 만약 새로운 데이터가 들어오면 이전 데이터는 취소`() = runTest {
        // given
        val state = Channel<Int>(capacity = CONFLATED)
        val result = mutableListOf<Int>()
        // when
        launch {
            state.send(1)
            state.send(2)
            state.send(3)
            delay(1)
            state.send(4)
            state.close()
        }
        launch {
            state.consumeEach {
                result.add(it)
            }
        }
        // then
        advanceUntilIdle()
        result shouldBe listOf(3, 4)
    }
}