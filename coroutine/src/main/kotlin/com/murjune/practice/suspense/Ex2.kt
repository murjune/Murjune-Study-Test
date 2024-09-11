package com.murjune.practice.suspense

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun main() {
    runBlocking {
        val job = launch {
            while (true) {
                println("While in ${Thread.currentThread().name}")
                delay(100L)
            }
        }
        println(1)
    }
}
