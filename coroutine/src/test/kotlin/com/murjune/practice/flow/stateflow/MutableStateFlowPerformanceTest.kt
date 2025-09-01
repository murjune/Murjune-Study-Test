package com.murjune.practice.flow.stateflow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis
import kotlin.test.Test

/**
 * MutableStateFlow 성능 테스트
 * - update
 * - setValue
 * - Monitor Lock 기반의 updateByMonitorLock
 *
 *  속도 성능 -> 차이 없음
 *  데이터 일관성 -> updateByMonitorLock, update 만 보장됨
 * */
class MutableStateFlowPerformanceTest {

    @Test
    fun test_update_performance_singleThread() = runBlocking {
        val iterations = 30_000_000
        val stateFlow = MutableStateFlow(0)

        val time = measureTimeMillis {
            repeat(iterations) {
                stateFlow.update {
                    it + 1
                }
            }
        }

        println("MutableStateFlow update_singleThread: ${stateFlow.value} - $time ms")
    }

    @Test
    fun test_updateByMonitorLock_performance_singleThread() = runBlocking {
        val iterations = 30_000_000
        val stateFlow = MutableStateFlow(0)

        val time = measureTimeMillis {
            repeat(iterations) {
                stateFlow.updateByMonitorLock {
                    it + 1
                }
            }
        }

        println("MutableStateFlow updateByMonitorLock_singleThread: ${stateFlow.value} - $time ms")
    }

    @Test
    fun test_setValue_performance_singleThread() = runBlocking {
        val iterations = 30_000_000
        val stateFlow = MutableStateFlow(0)

        val time = measureTimeMillis {
            repeat(iterations) {
                stateFlow.value = stateFlow.value + 1
            }
        }

        println("MutableStateFlow setValue_singleThread: ${stateFlow.value} - $time ms")
    }

    @Test
    fun test_update_performance_multiThread() = runBlocking {
        val iterations = 100_000
        val stateFlow = MutableStateFlow(0)

        val time = measureTimeMillis {
            List(iterations) {
                launch(Dispatchers.Default) {
                    stateFlow.update {
                        it + 1
                    }
                }
            }.joinAll()
        }

        println("MutableStateFlow update: ${stateFlow.value} - $time ms")
    }

    @Test
    fun test_updateByMonitorLock_performance_multiThread() = runBlocking {
        val iterations = 100_000
        val stateFlow = MutableStateFlow(0)

        val time = measureTimeMillis {
            List(iterations) {
                launch(Dispatchers.Default) {
                    stateFlow.updateByMonitorLock {
                        it + 1
                    }
                }
            }.joinAll()
        }

        println("MutableStateFlow updateByMonitorLock: ${stateFlow.value} - $time ms")
    }

    @Test
    fun test_setValue_performance_multiThread() = runBlocking {
        val iterations = 100_000
        val stateFlow = MutableStateFlow(0)

        val time = measureTimeMillis {
            List(iterations) {
                launch(Dispatchers.Default) {
                    stateFlow.value = stateFlow.value + 1
                }
            }.joinAll()
        }

        println("MutableStateFlow setValue: ${stateFlow.value} - $time ms")
    }

    /**
     * Spin Lock 기반이 아닌 Monitor Lock 기반의 동기화
     * */
    private inline fun <T> MutableStateFlow<T>.updateByMonitorLock(function: (T) -> T) {
        synchronized(this) {
            value = function(value)
        }
    }
}