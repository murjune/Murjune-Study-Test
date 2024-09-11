package com.murjune.practice.suspense

import com.murjune.practice.utils.launchWithElapsedTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.coroutineContext

private suspend fun suspendFuncA() {
    delay(300)
    print("Hello ")
}

private suspend fun suspendFuncB() {
    delay(200)
    print("Odooong ")
}

private suspend fun suspendFunAWithCoroutineScope() {
    CoroutineScope(coroutineContext).launch {
        delay(300)
        println(1)
    }
}

private suspend fun suspendFunBWithCoroutineScope() {
    CoroutineScope(coroutineContext).launch {
        delay(200)
        println(2)
    }
}

suspend fun parallelSuspendFunc() = coroutineScope {
    launch {
        suspendFuncA()
    }
    launch {
        suspendFuncB()
    }
}

suspend fun parallelSuspendFuncWithCoroutineScope() = supervisorScope {
    launch {
        suspendFuncA()
        throw Exception("error")
    }
    launch {
        suspendFuncB()
    }
}

fun main() {
    runBlocking {
        launchWithElapsedTime("suspend") {
            suspendFuncA()
            suspendFuncB()
        }.join()
        // output: Hello Odooong
        launchWithElapsedTime("suspend + coroutineScope") {
            suspendFunAWithCoroutineScope()
            suspendFunBWithCoroutineScope()
        }.join()
        // output: Odooong Hello
        launchWithElapsedTime("suspend + launch") {
            launch {
                suspendFuncA()
            }
            launch {
                suspendFuncB()
            }
        }.join()
        // output: Odooong Hello
        launchWithElapsedTime("suspend + coroutineScope") {
            parallelSuspendFunc()
        }.join()
        launchWithElapsedTime("suspend + supervisorScope") {
            parallelSuspendFuncWithCoroutineScope()
        }
    }
}
