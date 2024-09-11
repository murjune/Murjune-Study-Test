package com.murjune.practice.sharedflow

import com.murjune.practice.utils.launchWithName
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SharedFlowTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SharedFlow 에서 onEmpty() 는 절대 작동하지 않는다`() = runTest {
        val sharedFlow = MutableSharedFlow<Int>()
        val res = mutableListOf<Int>()

        sharedFlow
            .onEmpty { emit(0) }
            .onEach {
                delay(100)
                println("Collect $it")
                res.add(it)
            }.launchIn(backgroundScope)
        advanceUntilIdle()
        res shouldBe emptyList()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SharedFlow 에서 onCompletion() 는 절대 작동하지 않는다`() = runTest {
        val sharedFlow = MutableSharedFlow<Int>()
        val res = mutableListOf<Int>()

        launchWithName("Collector") {
            sharedFlow.onEmpty { emit(0) }.onCompletion {
                res.add(1)
                println("onCompletion")
            }.collect {
                println("Collect $it")
            }
        }
        launchWithName("Producer") {
            delay(10)
            sharedFlow.emit(1)
        }
        advanceUntilIdle()
        coroutineContext.cancelChildren()
        res shouldBe emptyList()
    }
}