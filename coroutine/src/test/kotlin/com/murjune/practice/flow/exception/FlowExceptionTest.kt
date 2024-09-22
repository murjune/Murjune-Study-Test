package com.murjune.practice.flow.exception

import com.murjune.practice.utils.runErrorTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlowExceptionTest {

    @Test
    fun `catch 연산자를 통해 Upstream 에러를 처리한다`() = runTest {
        var error: Throwable? = null
        flow<Int> {
            emit(1)
            error("error")
        }.catch {
            error = it
        }.launchIn(this)

        advanceUntilIdle()
        error.shouldNotBeNull()
        error?.message shouldBe "error"
    }


    @Test
    fun `‼️ catch 연산자는 DownStream 에러를 처리하지 못한다`() = runErrorTest<IllegalStateException> {
        var error: Throwable? = null
        flow<Int> {
            emit(1)
        }.catch {
            error = it
        }.onEach {
            error("error")
        }.launchIn(this)

        advanceUntilIdle()
        error.shouldBeNull() // 여기까지 도달할 수 없다.
    }

    @Test
    fun `잡히지 않는 예외가 발생 시 Flow 는 즉시 취소 하며, collect 에서 예외를 던진다`() =
        runErrorTest<IllegalStateException> {
            flow<Int> {
                emit(1)
                error("error")
            }.collect {
                println(it)
            }
        }

    @Test
    fun `✅collect 에서 던지는 예외는 try-catch 블록으로 잡을 수 있다`() = runTest {
        var error: Throwable? = null
        val res = mutableListOf<Int>()
        try {
            flow<Int> {
                emit(1)
                emit(2)
                emit(3)
            }.collect {
                if (it == 3) error("error")
                res.add(it)
            }
        } catch (e: Throwable) {
            error = e
        }
        error.shouldNotBeNull()
        error.message shouldBe "error"
        res shouldBe listOf(1, 2)
    }

    @Test
    fun `✅ collect 에서 예외가 발생할 위험이 있는 경우, onEach 에 연산을 옮기고 catch 연산자를 사용하는 것이 좋다`() = runTest {
        var error: Throwable? = null
        val res = mutableListOf<Int>()
        flow<Int> {
            emit(1)
            emit(2)
            emit(3)
        }.onEach {
            if (it == 3) error("error")
            res.add(it)
        }.catch {
            error = it
        }.collect()

        error.shouldNotBeNull()
        error?.message shouldBe "error"
        res shouldBe listOf(1, 2)
    }
}