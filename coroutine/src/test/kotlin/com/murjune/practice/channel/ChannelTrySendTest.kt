package com.murjune.practice.channel

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChannelTrySendTest {

    @Test
    fun `trySend() 는 Buffer 가 비어있을 때만 성공한다`() = runTest {
        // given
        val capacity = 2
        val channel = Channel<Int>(capacity)
        val result = mutableListOf<Int>()
        // when
        launch {
            repeat(5) {
                channel.trySend(it)
                delay(10)
            }
            channel.close()
        }
        launch {
            channel.consumeEach {
                result.add(it)
                delay(3)
            }
        }
        advanceUntilIdle()
        // then
        result shouldHaveSize 5
        result shouldBe listOf(0, 1, 2, 3, 4)
    }

    @Test
    fun `trySend() 는 Buffer 크기가 0이면 항상 실패한다`() = runTest {
        // given
        val channel = Channel<Int>()
        launch {
            repeat(10) { channel.trySend(1).isFailure.shouldBeTrue() }
            channel.close()
        }
        launch {
            channel.consumeEach { println(it) }
        }
    }
}