package com.murjune.practice.mockk

import io.kotest.matchers.shouldBe
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MockInjectTest {
    @SpyK
    private var engine = Engine()

    private var name = ""

    private var price = 1

    @InjectMockKs
    private lateinit var car: Car

    fun `test`() {
        car.name shouldBe name
    }

    @Test
    fun `test2`() {
        // given

        // when

        // then
    }

    class Engine {
        fun start() {
            println("Engine start")
        }
    }

    class Car(private val engine: Engine, val name: String, private val price: Int) {
        private val age: Int = 0

        fun start() {
            engine.start()
            println("Car $name start")
            println("Car price is $price")
        }
    }
}
