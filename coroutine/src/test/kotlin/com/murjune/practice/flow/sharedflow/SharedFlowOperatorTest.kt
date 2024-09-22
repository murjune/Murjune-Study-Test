package com.murjune.practice.flow.sharedflow

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class SharedFlowOperatorTest {

    @Test
    fun `SharedFlow 에서 onStart() 를 사용하여 초기값을 설정할 수 있다`() = runTest {
        // given
        val sharedFlow = MutableSharedFlow<Int>()
        val res = mutableListOf<Int>()
        // when
        sharedFlow.onStart { emit(0) }.onEach {
            res.add(it)
        }.launchIn(backgroundScope)
        // then
        runCurrent()
        res shouldBe emptyList()
    }

    @Test
    fun `‼️ SharedFlow 에서 onEmpty() 는 절대 작동하지 않는다`() = runTest {
        // given
        val sharedFlow = MutableSharedFlow<Int>()
        val res = mutableListOf<Int>()
        // when
        sharedFlow
            .onEmpty { emit(0) }
            .onEach {
                res.add(it)
            }.launchIn(backgroundScope)
        // then
        runCurrent()
        res shouldBe emptyList()
    }

    @Test
    fun `‼️ SharedFlow 에서 onCompletion() 취소될 때만 작동한다`() = runTest {
        // given
        val sharedFlow = MutableSharedFlow<Int>()
        val res = mutableListOf<Int>()
        // when
        sharedFlow
            .onCompletion { cause ->
                cause.shouldBeInstanceOf<CancellationException>()
            }
            .onEach {
                res.add(it)
            }.launchIn(backgroundScope)
        // then
        runCurrent()
        res shouldBe emptyList()
    }

}