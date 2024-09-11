package com.murjune.practice.mockk

import io.kotest.assertions.throwables.shouldThrow
import io.mockk.MockKException
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.mockk.verifyAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

// @MockKExtension.ConfirmVerification
// 모든 테스트 함수에 confirmVerified 를 추가할 필요가 없다.
@ExtendWith(MockKExtension::class)
class MockkAnnotationTest {
    enum class Direction { NORTH, SOUTH }

    enum class RoadType { HIGHWAY }

    enum class OutCome { OK }

    class Car() {
        private fun a() {
            println("a")
        }

        fun drive(direction: Direction): OutCome {
            throw UnsupportedOperationException("Not implemented")
        }

        fun noReturnFunction(
            speed: Double,
            direction: Direction,
            roadType: RoadType?,
        ) {
        }
    }

    @MockK
    private lateinit var car: Car

    @MockK(relaxed = true) // 모든 함수가 relaxed 되어있음
    private lateinit var car2: Car

    @MockK(relaxUnitFun = true) // 반환값이 없는 함수만 relaxed 되어있음
    private lateinit var car3: Car

    @RelaxedMockK // 모든 함수가 relaxed 되어있음 ==  @MockK(relaxed = true)
    private lateinit var car4: Car

    // val로 쓰면 안됨
    @SpyK
    private var car5: Car = Car() // 실제 객체를 spy 객체로 만들어준다, 실제 객체를 사용하지만 verify 를 사용할 수 있다.

    @Test
    @DisplayName("Mockk 어노테이션으로 Mockk 객체를 생성할 수 있음, 더 간단 하다!")
    fun `@Mockk`() {
        // given
        every { car.drive(Direction.NORTH) } returns OutCome.OK
        // when
        car.drive(Direction.NORTH)
        // then
        verify { car.drive(Direction.NORTH) }
        confirmVerified(car)
    }

    @Test
    @DisplayName("RelaxedMockK 는 모든 함수가 relaxed 되어있음")
    fun `@RelaxedMockK and Mockk(relaxed = true)`() {
        // when
        car2.drive(Direction.NORTH)
        car4.drive(Direction.NORTH)
        // then
        verifyAll {
            car4.drive(Direction.NORTH)
            car2.drive(Direction.NORTH)
        }
    }

    @Test
    @DisplayName("RelaxUnitFun 은 반환값이 없는 함수만 relaxed 되어있음")
    fun `RelaxUnitFun`() {
        car3.noReturnFunction(2.0, Direction.NORTH, RoadType.HIGHWAY)
        shouldThrow<MockKException> {
            car3.drive(Direction.NORTH)
        }

        verify { car3.noReturnFunction(2.0, Direction.NORTH, RoadType.HIGHWAY) }
        verify { car3.drive(Direction.NORTH) }
    }

    @Test
    @DisplayName("Spyk 는 실제 객체를 spy 객체로 만들어준다")
    fun `@Spyk`() {
        shouldThrow<UnsupportedOperationException> {
            car5.drive(Direction.NORTH)
        }
        verify { car5.drive(Direction.NORTH) }
    }
}
