package com.murjune.practice.stlib

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CharArrayTest {
    @Test
    fun `String() 생성자에 CharArray를 넣으면 문자열로 변환된다`() {
        // given
        val charArray = charArrayOf('1', '2', '3')

        // when
        val result = String(charArray)

        // then
        result shouldBe "123"
    }

    @Test
    fun `jointToString()을 사용하면 CharArray를 문자열로 변환할 수 있다`() {
        // given
        val charArray = charArrayOf('1', '2', '3')
        // when
        val result = charArray.joinToString("")
        // then
        result shouldBe "123"
    }
}