package com.murjune.practice.collection

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlin.test.Test


class ImmutableTest {
    data class Car(val car: String, val distance: Int = 0)

    @Test
    fun mutableTest() {
        val mutableList = mutableListOf<Car>()
        mutableList.add(Car("오둥"))
        mutableList.shouldContain(Car("오둥"))
    }

    @Test
    fun immutableList() {
        val list: List<Car> = listOf()
        val newList = list.plus(Car("오둥"))
        // then
        newList.shouldContainAll(listOf(Car("오둥")))
    }

    @Test
    fun mutableCars() {
        val input = listOf("알", "송")
        val cars = mutableListOf<Car>()
        input.forEach {
            cars.add(Car(it))
        }
        cars.shouldContainAll(listOf(Car("알"), Car("송")))
    }

    @Test
    fun getWinner() {
        val cars =
            listOf(
                Car("a", 10),
                Car("b", 20),
                Car("c", 30),
            )
        val winnerCar = cars.maxBy { it.distance }
        winnerCar shouldBe Car("c", 30)
    }

    @Test
    fun getWinnerByReduce() {
        val cars =
            listOf(
                Car("a", 10),
                Car("b", 20),
                Car("c", 30),
            )
        val winnerCar = cars.reduce { acc, car ->
            if (acc.distance > car.distance) acc else car
        }
        winnerCar shouldBe Car("c", 30)
    }
}