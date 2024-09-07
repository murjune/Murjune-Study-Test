package com.murjune.practice.mockk

import io.kotest.assertions.throwables.shouldThrow
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

// https://mockk.io/
class MockkStudyTest {
    enum class Direction { NORTH, SOUTH }

    enum class OutCome { OK }

    class Car() {
        fun drive(direction: Direction): OutCome {
            throw UnsupportedOperationException("Not implemented")
        }
    }

    /**
     * 기본적으로 Mockk 은 매우 엄격하다.
     * 따라서, 호출하고자 하는 함수에 모두 행동을 명시해 줘야 한다
     *
     * confirmVerified: verify 를 통해 호출한 함수가 없다면, confirmVerified 를 통해 확인할 수 있다.
     * 만약, mockk 로 만들어진 객체의 함수 호출 여부를 verify 로 검사하지 않았으면 `java.lang.AssertionError: Verification acknowledgment failed
     *` 가 발생한다
     * java.lang.AssertionError: Verification acknowledgment failed
     * */
    @Test
    @DisplayName("기본적인 Mockk 사용법, verify, confirmVerified 사용법을 확인한다")
    fun `test`() {
        // given
        val car = mockk<Car>(relaxed = true)
        // when
        car.drive(Direction.NORTH)
        // then
        verify { car.drive(Direction.NORTH) }
        confirmVerified(car)
    }

    @Test
    @DisplayName("만약, mockk 로 만들어진 객체의 함수 호출 여부를 verify 로 검사하지 않았으면 AssertionError 가 발생한다")
    fun `test2`() {
        // given
        val car =
            mockk<Car> {
                every { drive(Direction.NORTH) } returns OutCome.OK
            }
        // when
        car.drive(Direction.NORTH)
        // then
        shouldThrow<AssertionError> { confirmVerified(car) }
    }
}
