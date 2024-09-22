package com.murjune.practice.flow.eventflow

import com.murjune.practice.utils.MutableEventFlow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EventFlowTest {
    @Test
    fun `EventFlow 는 소비할 때까지 element 가 삭제되지 않는다`() = runTest {
        // given
        val eventFlow = MutableEventFlow<Int>()
        val res = mutableListOf<Int>()
        // when
        launch {
            eventFlow.emit(1)
            delay(10)
        }
        eventFlow
            .onEach {
                res.add(it)
            }
            .launchIn(backgroundScope)
        // then
        advanceTimeBy(10)
        runCurrent()
        res shouldBe listOf(1)
    }

    @Test
    fun `EventFlow 는 element 를 공유 Flow 가 아니다`() = runTest {
        // given
        val eventFlow = MutableEventFlow<Int>()
        val res = mutableListOf<Int>()
        val res2 = mutableListOf<Int>()
        // when
        eventFlow.emit(1)
        delay(10)
        // then
        eventFlow.onEach {
            res.add(it)
        }.launchIn(backgroundScope)

        eventFlow.onEach {
            res2.add(it)
        }.launchIn(backgroundScope)

        advanceTimeBy(10)
        runCurrent()
        // then
        res shouldBe listOf(1)
        res2 shouldBe emptyList()
    }
}