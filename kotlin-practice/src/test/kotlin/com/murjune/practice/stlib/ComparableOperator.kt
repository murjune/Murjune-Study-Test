package com.murjune.practice.stlib

import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ComparableOperator {
    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 10, 11])
    fun `coerceAtMost - num 보다 maxValue 가 크면 num 을 반환한다`(num: Int) {
        // given
        val maxValue = 11
        // when
        val actual = num.coerceAtMost(maxValue)
        // then
        actual shouldBe num
    }

    @ParameterizedTest
    @ValueSource(ints = [12, 13, 100])
    fun `coerceAtMost - num이 maxValue보다 커도  maxValue을 반환한다`(num: Int) {
        // given
        val maxValue = 11
        // when
        val actual = num.coerceAtMost(maxValue)
        // then
        actual shouldBe maxValue
    }

    @ParameterizedTest
    @ValueSource(ints = [12, 15, 100])
    fun `coerceAtLeast - num 보다 MinValue 가 크면 num 을 반환한다`(num: Int) {
        // given
        val minValue = 11
        // when
        val actual = num.coerceAtLeast(minValue)
        // then
        actual shouldBe num
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 10, 11])
    fun `coerceAtLeast - num 이 MinValue 보다 작아도 MinValue을 반환한다`(num: Int) {
        // given
        val minValue = 11
        // when
        val actual = num.coerceAtLeast(minValue)
        // then
        actual shouldBe minValue
    }
}