package com.murjune.practice.flow.operator

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

/**
 * merge 연산자는 여러개의 Flow 를 합쳐서 하나의 Flow 로 만들어준다. (1+1 -> 2)
 *
 * flow1: 1 -> 2 -> 3
 * flow2: 4 -> 5 -> 6
 *
 * merge(flow1, flow2) -> 1 -> 2 -> 3 -> 4 -> 5 -> 6
 *
 * 🚨 flow1 과 flow2 가 독립적으로 값을 방출한다.
 * flow1 이 delay 가 걸려있어도 flow2 가 delay 가 걸려있지 않다면 merge 된 Flow 는 flow2 를 계속 emit 하게 된다.
 *
 * ref: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/merge.html
 * */
@OptIn(ExperimentalCoroutinesApi::class)
class MergeOperatorTest {

    sealed interface UiEvent {
        object NavigateToDetail : UiEvent
        object NavigateToHome : UiEvent
        object Toast : UiEvent
    }

    @Test
    fun `2 개의 Flow 를 합친다`() = runTest {
        // given
        val flow1 = flowOf(1, 2, 3).onEach { delay(7) }
        val flow2 = flowOf(4, 5, 6).onEach { delay(15) }
        val result = mutableListOf<Int>()
        // when
        listOf(flow1, flow2).merge()
            .onEach { result.add(it) }
            .launchIn(this)
        // then
        advanceUntilIdle()
        val expected = listOf(1, 2, 4, 3, 5, 6)
        result shouldBe expected
    }

    @Test
    fun `하나의 Flow가 다른 Flow 의 방출을 Blocking 하지 않는다 - 즉, 각각 독립적으로 방출한다`() = runTest {
        // given
        val flow1 = flowOf(1).onEach { delay(1000) }
        val flow2 = flowOf(4, 5, 6)
        val flow3 = flowOf(7, 8, 9)
        val result = mutableListOf<Int>()
        // when
        merge(flow1, flow2, flow3)
            .onEach {
                result.add(it)
            }
            .launchIn(this)
        // then
        advanceUntilIdle()
        val expected = listOf(4, 5, 6, 7, 8, 9, 1)
        result shouldBe expected
    }

    @Test
    fun `✅ 여러 개의 이벤트를 합칠 때 유용하다 `() = runTest {
        // given
        val flow1 = flowOf(UiEvent.NavigateToDetail, UiEvent.Toast).onEach { delay(1000) }
        val flow2 = flowOf(UiEvent.NavigateToHome)
        val res = mutableListOf<UiEvent>()
        // when
        merge(flow1, flow2)
            .onEach {
                res.add(it)
            }
            .launchIn(this)
        // then
        advanceUntilIdle()
        val expected = listOf(UiEvent.NavigateToHome, UiEvent.NavigateToDetail, UiEvent.Toast)
        res shouldBe expected
    }

    @Test
    fun `❌ 다른 종류의 Flow 를 합칠 때는 사용하지 말자 `() = runTest {
        // given
        val flow1 = flowOf(1, 2, 3)
        val flow2 = flowOf("a", "b", "c")
        val result = mutableListOf<Any>()
        // when
        merge(flow1, flow2)
            .onEach {
                // 🚨it 은 Any 타입이다. 따라서, 다른 타입의 Flow 를 합칠 때는 사용하지 않는 것이 좋다.
                result.add(it)
            }
            .launchIn(this)
        // then
        advanceUntilIdle()
        val expected = listOf(1, 2, 3, "a", "b", "c")
        result shouldBe expected
    }
}