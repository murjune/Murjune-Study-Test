package com.murjune.practice.lamda.closure

class Ref<T>(var value: T)

private fun clickListener(): () -> Unit {
    var count = 0 // 불변성
    return {
        println(count++)
    }
}

var count = 0
private fun clickListener2(): () -> Unit {
    count++
    return {
        println(count)
    }
}

fun main() {

// JVM : Call Stack [] , Heap

//    val listeners = List(10) { clickListener() }
//    listeners.forEach { it.invoke() }

    clickListener()() // 커링

    val listeners = List(10) { clickListener2() }.forEach { it.invoke() }
}
