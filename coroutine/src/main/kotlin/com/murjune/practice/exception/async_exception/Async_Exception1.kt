package com.murjune.practice.exception.async_exception

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

// async 는 전파되는 예외 + await 호출부 두 곳에서 예외를 처리해야한다.

// 당연하겠지만, await() 를 호출하지 않아도 async 는 실행된다.
// 따라서, 예외처리 해줘야 함
private suspend fun ex1() = coroutineScope {
    async {
        throw RuntimeException("예외 발생")
    }
    launch {
        delay(100)
        println("launch 실행중")
    }
}

// await 호출부에서만 예외처리를 해주면 안된다
// async 에서 발생하는 예외가 부모 코루틴으로 전파되기 때문이다.
private suspend fun ex2() = coroutineScope {
    val deferred = async {
        throw RuntimeException("예외 발생")
    }
    try {
        deferred.await()
    } catch (e: Exception) {
        println("예외 처리")
    }
}

// supervisorScope 를 사용하여 async 에서 발생하는 예외를 부모까지 전파 X
// await 호출부에서만 예외처리를 해주면 된다.
private suspend fun ex3() = supervisorScope {
    val deferred = async {
        throw RuntimeException("예외 발생")
    }
    launch {
        try {
            deferred.await()
        } catch (e: Exception) {
            println("예외 처리")
        }
    }
}

fun main() {
    runBlocking {
        ex3()
    }
}
