package com.murjune.practice.flow.exception

import com.murjune.practice.utils.runErrorTest
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.plus
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SharedFlowExceptionTest {
    @Test
    fun `catch 연산자를 통해 예외 처리할 수 있다`() = runTest {
        // given
        val sharedFlow = MutableSharedFlow<Int>()
        val res = mutableListOf<Int>()
        var error: Throwable? = null
        // when
        sharedFlow
            .onStart { emit(1) }
            .onEach {
                error("error")
            }.catch {
                error = it
            }.onEach {
                res.add(it)
            }.launchIn(backgroundScope)
        // then
        runCurrent()
        res shouldBe emptyList()
        error.shouldBeInstanceOf<IllegalStateException>()
    }

    @Test
    @DisplayName("🚨테스트 프레임워크가 예외를 uncaught exception 으로 간주하여 테스트를 실패시킨다 따라서, runErrorTest 를 사용하여 예외를 처리한다")
    fun `CoroutineExceptionHandler 를 통해 예외 처리할 수 있다`() = runErrorTest<IllegalStateException> {
        // given
        val sharedFlow = MutableSharedFlow<Int>()
        val res = mutableListOf<Int>()
        var error: Throwable? = null
        val handler = CoroutineExceptionHandler { _, throwable ->
            error = throwable
        }
        sharedFlow
            .onStart { emit(1) }
            .onEach {
                error("error")
            }.onEach {
                res.add(it)
            }.launchIn(backgroundScope + handler)
        // then
        runCurrent()
        res shouldBe emptyList()
        error.shouldBeInstanceOf<IllegalStateException>()
    }

    @Test
    @DisplayName("🚨 catch 연산자는 예외가 발생할 때 그 예외를 처리하고, 플로우를 종료 시킨다, 그래서 다음 이벤트를 받을 수 없다")
    fun `‼️ sharedFlow 에서 catch 연산자의 한계`() = runTest {
        // given
        val refreshEvent = MutableSharedFlow<Unit>()
        val queryEvent = MutableStateFlow<String>("")
        val res = mutableListOf<String>()
        var isCompleted = false
        // when
        combine(refreshEvent.onStart { emit(Unit) }, queryEvent) { _, query ->
            println("query: $query")
            fetchPokemon(query)
        }.catch {
            println("error: $it")
        }.onEach {
            res.add(it)
        }.onCompletion {
            println(it)
            isCompleted = true
        }.launchIn(backgroundScope)
        runCurrent()
        queryEvent.value = "피카츄"
        // then
        runCurrent()
        res shouldBe emptyList()
        isCompleted.shouldBeTrue()
    }

    @Test
    @DisplayName("📚(이 방법 추천) try catch 블록을 사용하여 예외를 처리하면, 다음 이벤트를 받을 수 있다")
    fun `try-catch 를 활용한 예외처리`() = runTest {
        // given
        val refreshEvent = MutableSharedFlow<Unit>()
        val queryEvent = MutableStateFlow<String>("")
        val res = mutableListOf<String>()
        // when
        combine(refreshEvent.onStart { emit(Unit) }, queryEvent) { _, query ->
            println("query: $query")
            try {
                fetchPokemon(query)
            } catch (e: Exception) {
                println("error: $e")
                "error"
            }
        }.catch {
            println("error: $it")
        }.onEach {
            if (it == "error") return@onEach
            res.add(it)
        }.launchIn(backgroundScope)
        runCurrent()
        queryEvent.value = "피카츄"
        // then
        runCurrent()
        res shouldBe listOf("피카츄")
    }

    private suspend fun fetchPokemon(query: String): String {
        if (query.isEmpty()) error("query is empty")
        return listOf("피카츄", "라이츄", "파이리").firstOrNull { it == query }.orEmpty()
    }
}