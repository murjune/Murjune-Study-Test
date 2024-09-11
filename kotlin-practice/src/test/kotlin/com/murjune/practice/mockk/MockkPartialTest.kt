package com.murjune.practice.mockk

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MockkPartialTest {
    class Adder {
        fun addOne(num: Int) = num + 1
    }

    @Test
    @DisplayName("모킹한 객체의 일부 함수는 원래 함수를 호출하도록 할 수 있다")
    fun `answers + callOriginal`() {
        // given
        val adder = mockk<Adder>()
        every { adder.addOne(any()) } returns -1
        every { adder.addOne(3) } answers { callOriginal() }
        // when & then
        adder.addOne(0) shouldBe -1
        adder.addOne(3) shouldBe 4
    }
}
