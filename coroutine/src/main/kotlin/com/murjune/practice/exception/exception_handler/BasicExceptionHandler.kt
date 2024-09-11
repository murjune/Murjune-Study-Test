package com.murjune.practice.exception.exception_handler

import com.murjune.practice.utils.launchWithName
import com.murjune.practice.utils.log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// CoroutineExceptionHandler 는 처리되지 않는 예외만 처리한다.
// if) 자식 코루틴에 CoroutineExceptionHandler 이 설정되어있다.
// 자식 코루틴이 예외를 처리(try-catch) 혹은 부모로 예외를 전파(throw)하면 자식 코루틴은 예외를 처리한 것으로 보기에
// CoroutineExceptionHandler 은 발동되지 않는다.
// 이러한 특징 때문에 CoroutineExceptionHandler 은 다음과 같은 특징으로 정리 가능하다

// 1) CoroutineExceptionHandler 는 처리되지 않는 예외만 처리한다.
// 2) CoroutineExceptionHandler 은 예외 발생되는 부모 코루틴(Job)에 설정되어야한다.
// 따라서, 전역 코루틴 에러 처리에 자주 사용되는 친구이다.
// 이거 문제로 내자!!
private suspend fun ex1() = coroutineScope {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("[예외 발생] - $throwable")
    }
    launch(exceptionHandler) { // exceptionHandler 는 launch 로 인해 생성되는 job 에 적용된다.
        throw Exception("예외 발생")
    }
    delay(1000L)
}

// 따라서, exceptionHandler 는 스코프에 의해 만들어지는 Job 에 적용된다.
// CoroutineScope 에 Job 을 설정하지 않으면 내부적으로 Job() 을 통해 새로운 Job 을 생성한다.
private suspend fun ex2() = coroutineScope {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("[예외 발생] - $throwable")
    }
    launch(exceptionHandler) {
        launch {
            throw RuntimeException("예외 발생")
        }
    }
    delay(1000L)
    println("ex2 종료")
}

// CoroutineExceptionHandler 는 처리되지 않는 예외만 처리한다.
private suspend fun ex3() = coroutineScope {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("[예외 발생] - $throwable")
    }
    CoroutineScope(exceptionHandler).launch {
        try {
            throw RuntimeException()
        } catch (e: Exception) {
            println("예외 처리")
        }
    }
    delay(1000L)
    println("ex3 종료")
}

private suspend fun ex4() = coroutineScope {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("[예외 발생] - $throwable")
    }
    log("job: ${coroutineContext[Job]}")
    launch(exceptionHandler) {
        launchWithName("child1") {
            throw RuntimeException()
        }
        delay(100)
        launchWithName("child2") {
            delay(100)
            println("child2 실행중")
        }
        println("parent 실행중")
        delay(1000L)
        println("ex3 종료")
    }.join()
}

fun main() {
    runBlocking {
        ex2()
    }
}
