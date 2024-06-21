package com.murjune.practice.lamda.closure

//private fun outerFunction2() {
//    val outerVariable = "Outside"
//
//    fun innerFunction() {
//        val innerVariable = "Inside"
//        println(outerVariable)  // outerVariable 는 innerFunction() 에서 접근 가능!
//        println(innerVariable)  // innerVariable 는 innerFunction() 에서 접근 가능!
//    }
//
//    innerFunction()
//    println(outerVariable)
//    println(innerVariable)  // Error: innerVariable 는 innerFunction 의 밖에서 접근 불가능하다 ❌
//}

private fun outerFunction(): () -> String {
    val outerVariable = "outer 지역 변수"
    fun innerFunction(): String {
        return outerVariable // 자유 변수
    }
    return ::innerFunction
}

fun main() {
    val closure = outerFunction()
    println(closure()) // output: "outer 지역 변수"
}
