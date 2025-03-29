package com.murjune.practice.lamda.closure


private data class KotlinClosure(val increaseCount: () -> Unit, val printCnt: () -> Unit)

// Closure - JS 에서 이 형태로 Closure 를 자주 사용한다
private fun createClosure(): KotlinClosure {
    var cnt = 0

    fun increaseCount() = cnt++

    fun printCnt() = println(cnt)

    return KotlinClosure(::increaseCount, ::printCnt)
}

fun main() {
    val (increaseCount, printCnt) = createClosure()
    printCnt() // output: 0
    increaseCount()
    increaseCount()
    increaseCount()
    printCnt() // output: 3
}
