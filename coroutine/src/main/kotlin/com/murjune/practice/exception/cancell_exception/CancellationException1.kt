package com.murjune.practice.exception.cancell_exception

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

// CancellationException 는 예외가 발생해도 부모로 전파되지 않는다.
private suspend fun ex1() = coroutineScope {
    launch {
        throw CancellationException("예외 발생")
    }
    delay(100L)
    println(12)
}

// CancellationException 은 해당 코루틴에서만 발생하고 부모로 전파되지 않는다.
// 따라서, CoroutineExceptionHandler 를 통해 예외를 처리할 수 없다.
// 좋은 것이쥬
private suspend fun ex2() = coroutineScope {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("[예외 발생] - $throwable")
    }
    withTimeout(10L) {
        delay(20)
        println("launch 실행중")
    }
    delay(100L)
    println(12)
}

fun main() {
    runBlocking {
        delay(1000L)
        println(120)
    }
}
