package com.murjune.practice.channel

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChannelProduceOperatorTest {
    @Test
    fun `produce 함수를 활용하면 데이터를 생성하는 Producer 에서 예외가 발생하면 자동으로 Channel을 취소한다`() = runTest {
        val result = mutableListOf<Int>()
        val channel = produce<Int> {
            send(1)
            send(2)
            delay(100)
            send(3)
        }

        launch {
            channel.consumeEach {
                result.add(it)
            }
        }
        advanceUntilIdle()
        result shouldBe listOf(1, 2, 3)
    }
    // in 과 consumeEach 의 차이는 consumeEach 는 다 가져오고나서 !!  Channel을 취소시킨다는 차이점이 있다.
}