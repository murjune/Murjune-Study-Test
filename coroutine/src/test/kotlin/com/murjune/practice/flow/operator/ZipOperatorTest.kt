package com.murjune.practice.flow.operator

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

/**
 * zip 연산자는 두 개의 Flow 를 1:1 로 조합하여 새로운 Flow 를 만들어준다.
 *
 * flow1: 1 -> 2 -> 3
 * flow2: 4 -> 5 -> 6
 *
 * flow1.zip(flow2) -> (1 , 4) -> (2 , 5) -> (3 , 6)
 *
 * 🚨 (merge 는 1+1 -> 2 , zip 은 1+1 -> 1 느낌)
 * 🚨 low1 과 flow2 가 동시에 값을 방출할 때만 값을 방출한다. (둘은 운명 공동체)
 * flow1 이 1초만에 끝난다고 해도 flow2 가 100초 걸린다면 flow1 은 flow2 가 끝날 때까지 기다린다.
 *
 * ref: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/zip.html
 * */
@OptIn(ExperimentalCoroutinesApi::class)
class ZipOperatorTest {
    @Test
    fun `두 개의 Flow 를 조합하여 새로운 Flow 를 만든다`() = runTest {
        // given
        val flow1 = flowOf(1, 2, 3)
        val flow2 = flowOf(4, 5, 6)
        val res = mutableListOf<Int>()
        // when
        flow1.zip(flow2) { a, b -> a + b }
            .onEach {
                res.add(it)
            }.launchIn(this)
        // then
        advanceUntilIdle()
        val expected = listOf(5, 7, 9)
        res shouldBe expected
    }

    @Test
    fun `Zip 연산자는 두 Flow의 값을 항상 쌍으로 묶어서 방출한다`() = runTest {
        // given
        val flow1 = flowOf(1, 2, 3)
        val flow2 = flowOf("a", "b", "c", "d")
        val res = mutableListOf<String>()
        // when
        //
        //  1      2       3
        //  a      b       c      d(한쪽 대기)
        flow1.zip(flow2) { a, b ->
            "$a$b"
        }.onEach {
            res.add(it)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        runCurrent()
        // zip은 각 flow에서 하나씩만 소비하므로 짧은 쪽(flow1)에 맞춰 3개만 방출
        val expected = listOf("1a", "2b", "3c")
        res shouldBe expected
    }

    @Test
    fun `‼️ 하나의 Flow 는 다른 Flow 가 방출될 때까지 기다린다 - 즉, Blocking 된다`() = runTest {
        // given
        val flow1 = flowOf(1, 2, 3).onEach { delay(1000) }
        val flow2 = flowOf(4, 5, 6)
        val res = mutableListOf<Int>()
        // when
        flow1.zip(flow2) { a, b -> a + b }
            .onEach {
                res.add(it)
            }.launchIn(this)
        // then
        advanceUntilIdle()
        val expected = listOf(5, 7, 9)
        res shouldBe expected
        currentTime shouldBe 3000 // 🚨 flow2 는 flow1 이 끝날 때까지 기다렸다가 방출된다.
    }


    @Test
    fun `‼️ 만약 짝이 맞지 않으면 남은 값은 무시된다`() = runTest {
        // given
        val flow1 = flowOf(1, 2)
        val flow2 = flowOf(4, 5, 6)
        val res = mutableListOf<Int>()
        // when
        flow1.zip(flow2) { a, b -> a + b }
            .onEach {
                res.add(it)
            }.launchIn(this)
        // then
        advanceUntilIdle()
        val expected = listOf(5, 7)
        res shouldBe expected
    }

    @Test
    fun `‼️ 만약 짝이 맞지 않으면 남은 값은 무시된다 2`() = runTest {
        // given
        val flow1 = flowOf(1, 2, 3, 4,5).onEach {
            if (it == 3) {
                delay(50L)
            } else {
                delay(10)
            }
        }
        val flow2 = flowOf("a", "b", "c", "d").onEach { delay(15) }
        val res = mutableListOf<String>()
        // when
        // 10ms  20ms  30ms  40ms  50ms  60ms  70ms 80ms 90md
        //  1    2                               3   4    5(한쪽 대기)
        //    a        b      c       d
        flow1.zip(flow2) { a, b ->
            "$a$b"
        }.onEach {
            res.add(it)
        }.launchIn(this)
        // then
        advanceUntilIdle()
        // flow1이 2개만 있으므로 2개만 방출
        val expected = listOf("1a", "2b", "3c", "4d")
        res shouldBe expected
    }

}