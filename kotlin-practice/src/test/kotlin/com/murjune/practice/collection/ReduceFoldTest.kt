package com.murjune.practice.collection

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ReduceFoldTest {
    @Test
    fun `runningReduce 는 누적합을 계산하고, 그 결과를 리스트에 담아 반환한다`() {
        val result = listOf(1, 2, 3).runningReduce { acc, i -> acc + i }
        result shouldBe listOf(1, 3, 6)
    }

    @Test
    fun `reduce 는 누적합을 계산하고, 최종 누적합을 반환한다`() {
        val result = listOf(1, 2, 3).reduce { acc, i -> acc + i }
        result shouldBe 6
    }

    @Test
    fun `runningFold 는 초기값에 element를 더해나간후, 누적합의 결과를 리스트에 담아 반환한다`() {
        // given
        val initial = 0
        val list = listOf(1, 2, 3)
        val sumOperator: (Int, Int) -> Int = { acc, element -> acc + element }
        // when
        val actual = list.runningFold(initial, sumOperator)
        // then
        val expect = listOf(0, 1, 3, 6)
        actual shouldBe expect
    }

    @Test
    fun `flod 는 초기값에 element를 더해나간후, 최종 누적합을 반환한다`() {
        // given
        val initial = 0
        val list = listOf(1, 2, 3)
        val sumOperator: (Int, Int) -> Int = { acc, element -> acc + element }
        // when
        val actual = list.fold(initial, sumOperator)
        // then
        val expect = 6
        actual shouldBe expect
    }
}