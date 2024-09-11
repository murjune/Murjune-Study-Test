package com.murjune.practice.channel

import com.murjune.practice.utils.launchWithName
import com.murjune.practice.utils.log
import com.murjune.practice.utils.runErrorTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.channels.shouldBeClosed
import io.kotest.matchers.channels.shouldBeEmpty
import io.kotest.matchers.channels.shouldBeOpen
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

/**
 * Channel : 코루틴끼리의 통신을 위한 기본적인 방법으로 Hot Stream 이다.
 * Channel 은 공유 데이터를 통해 코루틴 간에 데이터를 전달하는 방법을 제공한다.
 * 쉽게 말해 Channel 은 공유 책장이다.
 *
 * 채널은 Producer 와 Consumer 사이의 통신을 위한 중간 매개체로 생각할 수도 있다.
 * 이때, Channel 의 Producer 와 Consumer 는 서로 다른 코루틴이다.
 * Producer 와 Consumer 의 수에 제한이 없다.
 *
 * SharedFlow 와 다른 점은 Channel을 통해 전송된 모든 값은 단 한 번만 소비된다.
 * */
@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
class ChannelBasicTest {
    // 각기 다른 코루틴에서 생산하는 값을 소비하는 예제
    // 하지만, 해당 예제에는 문제가 있다.
    // 수신자가 얼마나 많은 값을 받아야 하는지 알 수 없다.
    // 수신자 입장에서는 생산자가 얼마나 많은 값을 생산하는지 궁금해하지 않고 모르는 경우가 많다.
    // 따라서, 송신자가 보내는 만큼 수신자가 기다리고 받아야하는 것이 자명할 것이다.
    @Test
    fun `Channel 은 생산자과 소비를 위한 중간 매개체이다`() = runTest {
        val channel = Channel<Int>().apply { invokeOnClose { log("Channel is closed") } }
        launch(CoroutineName("Producer")) {
            repeat(5) { x ->
                log("produce $x")
                channel.send(x)
            }
        }
        launch(CoroutineName("Consumer")) {
            repeat(5) {
                val received = channel.receive()
                log("receive $received")
            }
        }
        advanceUntilIdle()
        channel.shouldBeOpen() // 아직 Channel 은 닫히지 않았다. (isReceiveClosed or isSendClosed 가 false 일 경우)
        channel.shouldBeEmpty() // Channel 은 비어 있음 - 만약 channel 이 Closed 되어 있으면 element 가 비어 있어도 isEmpty 는 false 다.
    }

    @Test
    fun `Channel 의 close 를 호출한 후에는 더 이상 데이터를 생산할 수 없다`() = runTest {
        // given
        val channel = Channel<Int>().apply { invokeOnClose { log("Channel is closed") } }
        // when
        channel.close()
        // then
        shouldThrow<ClosedSendChannelException> {
            channel.send(100)
        }
        channel.shouldBeClosed()
    }


    // close: 'close'를 여러 번 호출해도 첫 번째 호출 이후에는 추가 효과가 없습니다.
    // 닫기 토큰: close() 더 이상 요소가 전송되지 않음을 나타내는 특별한 "close Token"이 전송됩니다.
    //isClosedForSend 는 close 호출 후 즉시 true 가 됩니다.
    //isClosedForReceive 는 이전에 전송된 모든 요소가 수신된 후에만 true 가 됩니다.
    // 만약 isClosedForSend 이 false 인데 send 시 ClosedSendChannelException 이 발생
    // 만약 isClosedForReceive 이 false 인데 receive 시 ClosedReceiveChannelException 이 발생합니다.


    // 이와 같이 Producer 코루틴이 블록되는 이유는 오버플로의 옵션이 SUSPEND 이기 때문이다.
    // SUSPEND: 버퍼가 가득 차면 send 함수는 코루틴을 일시 중단시키고 버퍼가 비워질 때까지 대기 한다.
    // 이 부분은 다음 포스팅에서 더 자세하게 다루겠다!
    @Test
    fun `Never closed Channel - send 함수는 Channel 의 buffer 가 빌 때까지 해당 코루틴을 블록시킨다`() = runTest {
        val channel = Channel<Int>()
        launchWithName("Producer") {
            repeat(5) { x ->
                log("produce $x")
                channel.send(x)
            }
            channel.close() // unReached.. Channel 에 있는 buffer 가 빌 때까지 send 를 대기하기 때문이다.
        }
        launchWithName("Consumer") {
            repeat(4) {
                val received = channel.receive()
                log("receive $received")
            }
            channel.cancel()
        }
    }

    // 부모 코루틴을 cancel 시키면 send 는 취소된다.
    @Test
    fun `부모 코루틴을 cancel 시켜, send() 작업을 취소시킨 후, Channel 을 닫는다`() = runTest {
        val channel = Channel<Int>().apply { invokeOnClose { log("Channel is closed") } }
        launch {
            withTimeout(1000) {
                repeat(5) { x ->
                    log("produce $x")
                    channel.send(x)
                }
            }
        }.invokeOnCompletion { channel.close() }
        launch {
            repeat(4) {
                val received = channel.receive()
                log("receive $received")
            }
        }
        advanceUntilIdle()
        channel.shouldBeClosed()
    }

    @Test
    fun `close() 함수를 호출하고 buffer 에 원소가 모두 비어있어야 Channel 이 종료된다`() = runTest {
        val channel = Channel<Int>().apply { invokeOnClose { log("Channel is closed") } }
        launch(CoroutineName("Producer")) {
            repeat(5) { x ->
                log("produce $x")
                channel.send(x)
            }
            channel.close()
        }
        launch(CoroutineName("Consumer")) {
            repeat(5) {
                val received = channel.receive()
                log("receive $received")
            }
        }
        advanceUntilIdle()
        channel.shouldBeClosed()
    }

    @Test
    fun `생산자 코루틴이 취소되면 send 는 취소된다 `() = runTest {
        // given
        val channel = Channel<Int>().apply { invokeOnClose { log("Channel is closed") } }
        // when
        launch {
            delay(100)
            repeat(5) { x ->
                log("produce $x")
                channel.send(x)
            }
        }.cancelAndJoin()
        // then
        channel.shouldBeOpen()
        channel.shouldBeEmpty()
    }

    // channel 을 for 문으로 순회하면서 값을 받아온다. Channel 이 iterator 를 구현하고 있기 때문에 가능하다.
    @Test
    fun `Channel Basic - Iterator 활용`() = runTest {
        val channel = Channel<Int>().apply { invokeOnClose { log("Channel is closed") } }
        // when
        launch(CoroutineName("Producer")) {
            repeat(5) { x ->
                log("produce $x")
                channel.send(x)
            }
            channel.close() // close 를 해주어야 Consumer 가 무한정 대기하지 않는다.
        }
        launch(CoroutineName("Consumer")) {
            for (received in channel) {
                log("receive $received")
            }
            log("Consumer is done")
        }
        // then
        advanceUntilIdle()
        channel.shouldBeClosed()
    }

    // 혹은 consumeEach 를 사용할 수도 있다.
    // 차이점은 consumeEach 는 buffer 에 있는 element 를 모두 소비하고 자동으로 cancel 된다.
    @Test
    fun `Channel Basic - consumeEach 활용`() = runTest {
        val channel = Channel<Int>().apply {
            this.invokeOnClose { log("Channel is closed") }
        }
        launch(CoroutineName("Producer")) {
            repeat(5) { x ->
                log("produce $x")
                channel.send(x)
            }
            channel.close()
        }

        launch(CoroutineName("Consumer")) {
            channel.consumeEach { received ->
                log("receive $received")
            }
        }
        advanceUntilIdle()
        channel.shouldBeClosed()
    }

    @Test
    fun `Channel Basic - consumeEach with exception handling`() = runTest {
        val channel = Channel<Int>()
        val result = mutableListOf<Int>()

        launch(CoroutineName("Producer")) {
            repeat(5) { x ->
                channel.send(x)
            }
            channel.close()
        }

        launch(CoroutineName("Consumer")) {
            try {
                channel.consumeEach { received ->
                    result.add(received)
                    if (received == 3) {
                        // consumeEach 블록 내에서 예외가 발생하면, ReceiveChannel 을 cancel() 시킨다
                        throw RuntimeException("Exception during processing")
                    }
                }
            } catch (e: RuntimeException) {
                log("Caught exception: ${e.message}")
            }
        }
        // Assertions
        advanceUntilIdle()
        result shouldContainExactly listOf(0, 1, 2, 3)
        channel.shouldBeClosed()
    }

    @Test
    fun `Channel Basic - for with exception handling`() = runTest {
        val channel = Channel<Int>()
        val result = mutableListOf<Int>()

        launch(CoroutineName("Producer")) {
            repeat(5) { x ->
                channel.send(x)
            }
            channel.close()
        }

        launch(CoroutineName("Consumer")) {
            try {
                for (received in channel) {
                    println("receive $received")
                    result.add(received)
                    if (received == 3) {
                        throw RuntimeException("Exception during processing")
                    }
                }
            } catch (e: RuntimeException) {
                println("Caught exception: ${e.message}")
            } finally {
                println("Consumer is done")
                channel.cancel() // Channel 을 취소하여 Channel 을 종료시킨다.
            }
        }
        // Assertions
        advanceUntilIdle()
        result shouldContainExactly listOf(0, 1, 2, 3)
        channel.shouldBeClosed()
    }
}

// close: Produce 는 이제 할 수 없지만, buffer 에 남아있는 데이터를 모두 Consume 한 후 Channel 이 닫힙니다.
// send() 를 통해 데이터를 전송하고 buffer 에 데이터가 남아있는 경우, close()를 호출해도 Channel 이 닫히지 않습니다.

// 데이터 생산이 정상적으로 완료되었습니다.
// cancle: Producer/Consumer 가 취소되어 버퍼에 남아있는 데이터를 모두 소비하지 않는다.(버퍼에 있는 데이터 유실)
//  이 함수는 채널을 닫고 채널에서 버퍼링된 모든 전송 요소를 제거합니다.
// SendChannel 측면의 isClosedForReceive 및 isClosedForSend가 참을 반환하기 시작합니다.
// 오류나 취소로 인해 즉시 종료됩니다.
