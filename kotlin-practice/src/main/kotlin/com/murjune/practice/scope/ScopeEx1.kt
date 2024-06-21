package com.murjune.practice.scope

object ScopeEx1 {
    @JvmStatic
    fun main(
        args: Array<String>,
        degree: Int,
    ) {
        val (des, color) =
            when {
                degree < 10 -> 1 to 3
                else -> 3 to 3
            }
        for ((i, e) in listOf(1, 2, 3).withIndex()) {
            println("$i : $e")
        }
    }
}

