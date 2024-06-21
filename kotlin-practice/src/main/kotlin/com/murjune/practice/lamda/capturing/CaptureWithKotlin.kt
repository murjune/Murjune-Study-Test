package com.murjune.practice.lamda.capturing

import kotlin.concurrent.thread

fun main() {
    var c = 1
    val lambda = { println(c++) }
    lambda()
    thread { lambda() }.join()
    println(c)
}
