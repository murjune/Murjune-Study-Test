package com.murjune.practice.mockk

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkObject
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MockkObjectTest {
    object Number {
        fun number() = 1
    }

    @Test
    @DisplayName("싱글톤 객체(object) 를 mockking 할 수 있다")
    fun `object mockk`() {
        // given
        mockkObject(Number) // object 를 mockkObject 로 만들어준다
        every { Number.number() } returns 2
        // when & then
        Number.number() shouldBe 2
    }
}
