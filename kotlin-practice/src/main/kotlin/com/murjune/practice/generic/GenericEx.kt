package com.murjune.practice.generic

open class Fruit
class Apple : Fruit()
class Banana : Fruit()

fun receiveFruitss(fruits2: MutableList<Fruit>) {
    println("Number of fruits: ${fruits2.size}")
}

fun receiveFruits(fruits: List<Fruit>) {
    println("Number of fruits: ${fruits.size}")
}

fun main() {
    val apples: MutableList<Apple> = mutableListOf(Apple())
//    receiveFruits(fruits2 = apples)
    val fruits: List<Apple> = listOf(Apple(), Apple())
    receiveFruits(fruits)   // Number of fruits: 2
}
