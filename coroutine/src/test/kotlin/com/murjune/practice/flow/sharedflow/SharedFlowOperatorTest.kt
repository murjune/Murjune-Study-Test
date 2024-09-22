package com.murjune.practice.flow.sharedflow

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

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
        res shouldBe listOf(0)
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

    @Test
    @DisplayName("📚 예제1 - refresh event 와 query event 를 combine을 활용해 조합하여 fetchPokemon 을 호출한다")
    fun `sample1`() = runTest {
        // given
        val refreshEvent = MutableSharedFlow<Unit>()
        val queryEvent = MutableStateFlow<String>("")
        val res = mutableListOf<String>()
        // when
        combine(refreshEvent.onStart { emit(Unit) }, queryEvent) { _, query ->
            println("query: $query")
            fetchPokemon(query)
        }.onEach {
            res.add(it)
        }.launchIn(backgroundScope)
        runCurrent()
        queryEvent.value = "피카츄"
        // then
        runCurrent()
        res shouldBe listOf("", "피카츄")
    }

    private suspend fun fetchPokemon(query: String): String {
        return listOf("피카츄", "라이츄", "파이리").firstOrNull { it == query }.orEmpty()
    }
}