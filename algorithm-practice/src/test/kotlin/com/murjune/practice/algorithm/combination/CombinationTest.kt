package com.murjune.practice.algorithm.combination

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CombinationTest {
    @Test
    fun `n 개 중 r 개 뽑기`() {
        // given
        val n = 3
        val r = 1
        // when
        val res = combination(n, r)
        // then
        res shouldBe listOf(listOf(1), listOf(2), listOf(3))
    }

    @Test
    fun `n 개 중 r 개 뽑기2`() {
        // given
        val n = 4
        val r = 2
        // when
        val res = combination(n, r)
        // then
        res shouldBe listOf(
            listOf(1, 2),
            listOf(1, 3),
            listOf(1, 4),
            listOf(2, 3),
            listOf(2, 4),
            listOf(3, 4)
        )
    }
}