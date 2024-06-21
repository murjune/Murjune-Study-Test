package com.murjune.practice.lamda.closure

private class NotClosureEx {

    fun outerFunction(): () -> String {
        return {
            val local = 2
            "1$local"
        }
    }
}

fun main() {
    val lambda = NotClosureEx().outerFunction()
    println(lambda())
}
