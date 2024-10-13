package com.murjune.practice.closure

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class ClosureTest {

    @Test
    fun `외부 변수를 참조하고 있지 않은 클로져는 싱글톤이다`() {
        // given
        val generateClosure = { { 2 } }
        val generateClosure2 = { { "2" } }
        // when & then
        generateClosure() shouldBe generateClosure()
        generateClosure2() shouldBe generateClosure2()
    }

    @Test
    fun `외부 가변 변수를 캡쳐링 하고 있는 클로져는 싱글톤이 아니다`() {
        // given
        var a = 0
        val generateClosure = { { a } }
        // when & then
        generateClosure() shouldNotBe generateClosure()
    }

    @Test
    fun `외부 final 변수를 캡쳐링 하고 있는 클로져는 싱글톤이 아니다`() {
        // given
        val a = 0
        val generateClosure = { { a } }
        // when & then
        generateClosure() shouldNotBe generateClosure()
    }
}