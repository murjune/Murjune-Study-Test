package com.murjune.practice.exception.exception_handler

import com.murjune.practice.utils.launchWithName
import com.murjune.practice.utils.log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

// SuperVisorJob 에 예외가 전파되지 않는 것은 맞지만
// 예외 정보는 전달 받기 때문에 CoroutineExceptionHandler 를 사용하면 예외를 처리할 수 있다.
private suspend fun ex1() = coroutineScope {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("[예외 발생] - $throwable")
    }
    val supervisorScope = CoroutineScope(SupervisorJob() + exceptionHandler)
    supervisorScope.launchWithName("parent1") {
        throw RuntimeException("예외 발생")
    }
    supervisorScope.launchWithName("parent2") {
        delay(100)
        log("parent2 실행중")
    }
    delay(200)
}

fun main() {
    runBlocking {
        ex1()
    }
}
