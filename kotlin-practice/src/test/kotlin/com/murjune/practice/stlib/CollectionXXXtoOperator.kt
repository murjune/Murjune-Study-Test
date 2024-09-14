package com.murjune.practice.stlib

import io.kotest.matchers.shouldBe
import kotlin.test.Test

/**
 * Kotlin Collection 에는 XXXto 연산자가 많다.
 * filterTo, filterNotTo, mapNotNullTo 등이 있는데 이는 조건에 맞는 요소를 '새로운 컬렉션'에 추가해주는 연산자이다.
 *
 * 쉽게 말해 어떤 연산을 함 -> (결과값) -> 새로운 컬렉션에 추가해준다.
 *
 * 뭔가 코테 문제를 풀 때 유용할 것 같다.
 * */
class CollectionXXXtoOperator {

    @Test
    fun `filterTo 는 조건에 맞는 요소를 새로운 컬렉션에 추가해준다 `() {
        // given
        val list = listOf(1, 2, 3, 4, 5)
        val result = mutableListOf<Int>()
        // when
        list.filterTo(result) { it % 2 == 0 }
        // then
        result shouldBe listOf(2, 4)
    }

    @Test
    fun `filterNotTo 는 조건에 맞지 않는 요소를 새로운 컬렉션에 추가해준다`() {
        // given
        val list = listOf(1, 2, 3, 4, 5)
        val result = mutableListOf<Int>()
        // when
        list.filterNotTo(result) { it % 2 == 0 }
        // then
        result shouldBe listOf(1, 3, 5)
    }

    @Test
    fun `mapNotNullTo 는 null이 아닌 요소를 새로운 컬렉션에 추가해준다`() {
        // given
        val list = listOf(1, null, 3, null, 5)
        val result = mutableListOf<Int>()
        // when
        list.mapNotNullTo(result) { it }
        // then
        result shouldBe listOf(1, 3, 5)
    }

    @Test
    fun `groupByTo 는 특정 키에 맞는 요소를 새로운 컬렉션에 추가해준다`() {
        // given
        val list = listOf(1, 2, 3)
        val result = mutableMapOf<Int, MutableList<Int>>()
        // when
        list.groupByTo(result, keySelector = { it }, valueTransform = { it * it })
        // then
        val expect = mapOf(1 to listOf(1), 2 to listOf(4), 3 to listOf(9))
        result shouldBe expect
    }
}