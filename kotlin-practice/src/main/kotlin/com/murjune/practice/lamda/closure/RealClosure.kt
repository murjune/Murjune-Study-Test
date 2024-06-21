package com.murjune.practice.lamda.closure


data class Closure(val increaseCount: () -> Unit, val printCnt: () -> Unit)

// Closure - JS 에서 이 형태로 Closure 를 자주 사용한다
fun createClosure(): Closure {
    var cnt = 0

    fun increaseCount() = cnt++

    fun printCnt() = println(cnt)

    return Closure(::increaseCount, ::printCnt)
}

fun main() {
    val (increaseCount, printCnt) = createClosure()
    printCnt() // output: 0
    increaseCount()
    increaseCount()
    increaseCount()
    printCnt() // output: 3
}
