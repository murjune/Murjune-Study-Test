package com.murjune.practice.continuation

import io.kotest.assertions.throwables.shouldThrow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.runTest
import kotlin.concurrent.thread
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.test.Test

class CancellableContinuationTest {

    @Test
    fun `중간에 cancel 호출 시, resume 이후에 CancellationException이 발생한다`() = runTest {
        var thread: Thread? = null
        shouldThrow<CancellationException> {
            suspendCancellableCoroutine<String> { continuation ->
                thread = thread {
                    Thread.sleep(200)
                    continuation.resume("First Success")
                    println("First Success")
                }

                continuation.cancel()
            }
        }

        thread?.join()
    }

    @Test
    fun `중간에 cancel 호출 시, resume 이후에 CancellationException이 발생하기 때문에 문제 없음`() = runTest {
        launch {
            suspendCancellableCoroutine<String> { continuation ->
                // 첫 번째 resume
                thread {
                    Thread.sleep(100)
                    continuation.resume("Success")
                    println("Success")
                }
                continuation.cancel()
            }
        }

        Thread.sleep(200)
    }

    @Test
    fun `중간에 cancel 호출 시, resume 이후에 resume을 1번 더 호출하면 IllegalStateException 발생2`() = runTest {
        launch {
            suspendCancellableCoroutine<String> { continuation ->
                continuation.resume("Success")
                shouldThrow<IllegalStateException> {
                    continuation.resume("Fail")
                }
            }
        }
    }

    @Test
    fun `중간에 cancel 호출 시, resume 이후에 resume을 1번 더 호출하면 IllegalStateException 발생 2`() = runTest {
        launch {
            suspendCancellableCoroutine<String> { continuation ->
                thread {
                    Thread.sleep(100)
                    continuation.resume("Success") // 여기서 CancellationException 발생
                    continuation.resume("Not Reached") // 도달 X
                }
                continuation.cancel()
            }
        }

        Thread.sleep(200)
    }
}