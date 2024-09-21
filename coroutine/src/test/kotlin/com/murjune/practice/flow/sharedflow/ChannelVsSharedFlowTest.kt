package com.murjune.practice.sharedflow

import com.murjune.practice.utils.launchWithName
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChannelVsSharedFlowTest {
    // 1. Rendezvous Channel이란?
    //Rendezvous Channel은 코틀린의 Channel에서 사용할 수 있는 여러 유형 중 하나입니다.
    // 이 채널은 버퍼가 없는 채널로, 송신자가 데이터를 보낼 때까지 수신자가 대기하고, 수신자가 데이터를 받을 때까지 송신자가 대기합니다.
    // 즉, 송신과 수신이 동시에 일어나야만 데이터가 전달되는 채널입니다. 이 특징 때문에 Rendezvous(프랑스어로 "만남"이라는 의미)가 붙었습니다.
    @Test
    fun `채널은 핫 스트림이다 - 랑데부하다`() = runTest {
        val channel = Channel<Int>()
        val result = mutableListOf<Int>()
        launchWithName("Producer") {
            repeat(3) {
                println("send $it")
                channel.send(it)
                delay(100)
            }
            channel.close()
        }

        launchWithName("Consumer") {
            delay(1000)
            channel.consumeEach {
                println("receive $it")
                result.add(it)
            }
        }
        advanceUntilIdle()
        result shouldBe listOf(0, 1, 2)
    }

    // SharedFLow는 랑데부하지 않는다. 따라서, 버퍼가 없어도 송신자가 데이터를 보내고 수신자가 없으면 데이터는 유실된다

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SharedFlow 는 collector 가 없으면 데이터가 유실된다 `() = runTest {
        val flow = MutableSharedFlow<Int>()
        val result = mutableListOf<Int>()

        launchWithName("Producer") {
            repeat(3) {
                println("produce $it")
                delay(70)
                flow.emit(it)
            }
        }

        launchWithName("Consumer") {
            delay(200)
            flow.collect {
                println("collect $it")
                result.add(it)
            }
        }
        advanceUntilIdle()
        coroutineContext.cancelChildren()
        result shouldBe listOf(2)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SharedFlow + buffered - 생산자와 수신자는 병렬로 일한다 `() = runTest {
        val flow = MutableSharedFlow<Int>(1)
        val result = mutableListOf<Int>()

        launchWithName("Producer") {
            repeat(3) {
                delay(100)
                println("produce $it")
                flow.tryEmit(it)
            }
        }

        launchWithName("Consumer") {
            flow.buffer().collect {
                delay(2000)
                println("collect $it")
                result.add(it)
            }
        }
        advanceUntilIdle()
        coroutineContext.cancelChildren()
        result shouldBe listOf(0, 1, 2)
    }

    @Test
    fun `Clod Stream - Collect `() = runTest {
        val flow = flow<Int> {
            repeat(3) {
                emit(it)
                println("send $it - ${currentTime}")
                delay(100)
            }
        }
        delay(100)
        launchWithName("Consumer") {
            flow.collect {
                delay(2000)
                println("collect $it - ${currentTime}")
            }
        }
    }


    @Test
    fun `Hot Stream - buffer() 연산자를 활용햐서 flow 를 channel처럼 연산되게 할 수 있다 `() = runTest {
        val flow = flow<Int> {
            repeat(3) {
                emit(it)
                println("send $it")
                delay(100)
            }
        }
        delay(100)
        launchWithName("Consumer") {
            flow.collect {
                delay(2000)
                println("collect $it")
            }
        }
    }

    @Test
    fun `Hot Stream - ChannelFlow `() = runTest {
        val result = mutableListOf<Int>()
        // when
        val flow = channelFlow<Int> {
            repeat(3) {
                send(it)
                println("send $it")
                delay(100)
            }
        }
        delay(100)
        launchWithName("Consumer") {
            flow.collect {
                delay(2000)
                println("collect $it -$currentTime")
                result.add(it)
            }
        }
        // then
        advanceUntilIdle()
        coroutineContext.cancelChildren()
        result shouldBe listOf(0, 1, 2)
    }
}