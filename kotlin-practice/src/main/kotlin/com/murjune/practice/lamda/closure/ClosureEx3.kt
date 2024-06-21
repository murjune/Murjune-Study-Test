package com.murjune.practice.lamda.closure


private class ClosureEx3 {
    private val memberVariance = "멤버변수"
    fun outerFunction(): () -> String {
        return { memberVariance }
    }
}

fun main() {
    val lambda = ClosureEx3().outerFunction()
    println(lambda())
}
