package com.murjune.practice.conversion

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import kotlin.test.Test

class ArrayConversionTest {
    @Test
    fun `List to Array`() {
        // given
        val list = listOf(1, 2, 3)
        // when
        val array = list.toTypedArray()
        // then
        array.shouldBeTypeOf<Array<Int>>()
        array.shouldContainExactly(1, 2, 3)
    }
    @Test
    fun `List to IntArray`() {
        // given
        val list = listOf(1, 2, 3)
        // when
        val array = list.toIntArray()
        // then
        array.shouldBeTypeOf<IntArray>()
        array shouldBe intArrayOf(1, 2, 3)
    }
}