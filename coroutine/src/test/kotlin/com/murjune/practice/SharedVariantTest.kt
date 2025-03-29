package com.murjune.practice

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.test.Test

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class SharedVariantTest {

    @Test
    fun `count - Thread`() {
        var count = 0
        thread {
            repeat(1000) {
                count++
            }
        }
        count shouldBe 0
    }

    @Test
    fun `count - Thread_join`() {
        var count = 0
        thread {
            repeat(1000) {
                count++
            }
        }.join()
        count shouldBe 1000
    }

    @Test
    fun `count - Thread + Sleep`() {
        var count = 0
        repeat(1000) {
            thread {
                Thread.sleep(10)
                count++
            }
        }
        Thread.sleep(300)
        count shouldNotBe 1000
    }

    @Test
    fun `count - delay`() = runTest {
        var count = 0
        List(1000) {
            launch {
                count++
                delay(10)
            }
        }
        advanceUntilIdle()
        count shouldBe 1000
    }

    @Test
    fun `count - joinAll`() = runTest {
        var count = 0
        List(1000) {
            launch {
                count += 1
                delay(100)
            }
        }.also { it.joinAll() }

        count shouldBe 1000
    }

    @Test
    fun `count - CoroutineScope + Default ThreadPool`() = runTest {
        var count = 0
        CoroutineScope(Dispatchers.Default).launch {
            List(1000) {
                launch {
                    count++
                }
            }
        }
        count shouldBe 0
    }

    @Test
    fun `count - CoroutineScope + coroutineContext + Default ThreadPool`() = runTest {
        var count = 0
        CoroutineScope(Dispatchers.Default).launch {
            List(1000) {
                launch {
                    count++
                }
            }
        }
        count shouldBe 0
    }

    @Test
    fun `count - withContext + Default ThreadPool`() = runTest {
        var count = 0
        withContext(Dispatchers.Default) {
            List(1000) {
                launch {
                    count++
                }
            }
        }
        count shouldNotBe 1000
    }

    @Test
    fun `count - withContext + Single ThreadPool`() = runTest {
        val customDispatcher = newSingleThreadContext("오둥")
        var count = 0
        withContext(customDispatcher) {
            List(1000) {
                launch {
                    count++
                }
            }
        }
        count shouldBe 1000
    }

    @Test
    fun `count - runBlocking + Default Dispatcher`() = runTest {
        withContext(Dispatchers.Default) {
            var count = 0
            runBlocking {
                List(1000) {
                    launch {
                        count++
                    }
                }
            }
            count shouldBe 1000
        }
    }

    @Test
    fun `count - Use Monitor ReentrantLock `() = runTest {
        val lock = ReentrantLock()
        var count = 0
        withContext(Dispatchers.Default) {
            List(1000) {
                launch {
                    lock.lock()
                    count++
                    lock.unlock()
                }
            }
        }
        count shouldBe 1000
    }

    @Test
    fun `count - Coroutine Mutex`() = runTest {
        val mutex = Mutex()
        var count = 0
        withContext(Dispatchers.Default) {
            List(1000) {
                launch {
                    mutex.withLock {
                        count++
                    }
                }
            }
        }
        count shouldBe 1000
    }
}
