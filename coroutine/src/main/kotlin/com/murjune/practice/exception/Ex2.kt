package com.murjune.practice.exception

import com.murjune.practice.utils.log
import kotlinx.coroutines.*

suspend fun main() {
    // 1. try-catch
    try {
        runBlocking {
            error("error 발생!")
        }
    } catch (e: IllegalStateException) {
        println("try-catch - 잡았다 요놈! ✌️")
    }

    // 2. CoroutineException Handler
    val coroutineScope = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("CoroutineExceptionHandler - 잡았다 요놈! ✌️")
    }
    CoroutineScope(coroutineScope).launch {
        error("error 발생!")
    }

    delay(100)
}