package com.murjune.practice.stlib

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CharTest {
    @Test
    fun `'0' 의 아스키코드는 48`() {
        val char = '0'
        char.code shouldBe 48
    }

    @Test
    fun `'0'를 활용해서 Int형 1을 '1' 로 변환`() {
        val charZero = '0'
        val charOne = '1'
        charZero + 1 shouldBe charOne
    }

    @Test
    fun `'0'의 아스키코드를 활용해서 Int형 1을 '1' 로 변환`() {
        // '0'의 아스키코드는 48
        val charOneCode = ('0'.code + 1)
        // 48 + 1 = 49
        val charOne = charOneCode.toChar()
        // 49 에 해당하는 문자는 '1'
        charOne shouldBe '1'
    }

    @Test
    fun `String 과 first() 를 사용해서 CharArray 를 Char 로 변환`() {
        // given
        val str = "1"
        // when
        val result = str.first()
        // then
        result shouldBe '1'
    }

    @Test
    fun `isDigit() 를 사용해서 Char 가 숫자인지 확인할 수 있다`() {
        // given
        val char = '1'
        // when
        val result = char.isDigit()
        // then
        result shouldBe true
    }

    @Test
    fun `isLetter() 를 사용해서 Char 가 알파벳인지 확인할 수 있다`() {
        // given
        val char = 'a'
        // when
        val result = char.isLetter()
        // then
        result shouldBe true
    }
}