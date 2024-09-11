package com.murjune.practice.suspense

import com.murjune.practice.utils.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private suspend fun ex1() {
    log("Before")
    suspendCoroutine<Unit> { }
    log("After") // Never reached
}

private suspend fun ex2() {
    log("1) Start ex2", true)
    suspendCoroutine<Unit> { cont ->
        log("2) start suspendCoroutine", true)
        cont.resume(Unit)
        log("3) end suspendCoroutine", true)
    }
    log("4) Finish ex2", true)
}

private suspend fun ex3() {
    log("1) Before", true)
    suspendCoroutine<Unit> { cont ->
        thread {
            log("2) start suspendCoroutine", true)
            cont.resume(Unit)
            log("4) end suspendCoroutine", true)
        }
    }
    log("3) After", true)
}

private suspend fun ex4() {
    suspend fun delay(time: Long) {
        suspendCoroutine<Unit> { cont ->
            Executors.newSingleThreadExecutor().submit {
                log("2) start suspendCoroutine", true)
                Thread.sleep(time)
                cont.resume(Unit)
                log("4) end suspendCoroutine", true)
            }
        }
    }
    log("1) Before", true)
    delay(1000L)
    log("3) After", true)
}

private suspend fun ex5() {
    println("Start ex5")
    var continuation: Continuation<Unit>? = null
    suspend fun suspendAndSet() {
        suspendCoroutine<Unit> {
            continuation = it
        }
    }
    suspendAndSet()
    continuation?.resume(Unit)
    println("Finish ex5")// Never reached
}

private suspend fun ex6() = coroutineScope {
    println("Start ex6")
    var continuation: Continuation<Unit>? = null
    launch {
        delay(1000L)
        continuation?.resume(Unit)
    }
    suspend fun suspendAndSet() {
        suspendCoroutine { cont ->
            continuation = cont
        }
    }
    suspendAndSet()
    println("Finish ex6")
}

val executors = Executors.newFixedThreadPool(10)
suspend fun main() {
//    ex5()
    println("시작")
    val job = executors.submit(Callable {
        List(10000000) { 1 }.sum()
    })
    var a = 1
    executors.execute {
        a = job.get()
    }
    println("오예1")
    println(a)
    executors.shutdown()
}
