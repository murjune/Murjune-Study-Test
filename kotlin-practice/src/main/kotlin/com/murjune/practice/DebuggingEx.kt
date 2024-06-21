package com.murjune.practice


fun main() {
    val a = ArrayDeque(listOf(1, 2, 3, 4, 5))
    while (a.isNotEmpty()) {
        val num = a.removeFirst()
        println(num)
    }
    val a2 = fun(a: Int): Int = 1
}
