package com.murjune.practice.utils

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

//suspend fun main() {
//    measureTimeMillis {
//        CoroutineScope(Dispatchers.Default + coroutineExceptionHandler).launch {
//            supervisorScope {
//                val a = listOf(
//                    async { delay(1000) },
//                    async { delay(400); error("에러 발생") },
//                    async { delay(600) },
//                )
////                a.map { it.await() } // 1000ms
//                a.awaitAll() // 400ms
//            }
//        }.join()
//
//    }.let(::println)
//}
fun main() {
    GlobalScope.launch(Dispatchers.Default) {
        delay(100)
        println("GlobalScope")
    }.cancel()

    GlobalScope.launch(Dispatchers.Default) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            println("잡았다 요놈 ! ✅ ${throwable.message}")
        }
        val job = Job(coroutineContext.job).apply {
            invokeOnCompletion {
                println("invokeOnCompletion")
            }
        }

        val j = CoroutineScope(job + coroutineExceptionHandler).launch {
            delay(100)
        }
        delay(10)
        // Completed or Cancelled
    }
    Thread.sleep(2000)
}
//
//    runBlocking {
//    val handler = CoroutineExceptionHandler { _, throwable ->
//        println("잡았다 요놈 ! ✅ ${throwable.message}")
//    }
//
//    CoroutineScope(CoroutineName("parent")).launch {
//        launch(CoroutineName("parent") + handler) {
//            supervisorScope {
//                launch(CoroutineName("child")) {
//                    error("error 발생 👻")
//                }
//            }
//        }
//    }
//
//        delay(100)
//    }
//}

suspend fun loadImages() = supervisorScope {
    coroutineContext
    async { loadImage() }
    async { loadImage() }
    launch { loadImage(); error("error 발생 👻") }
    async { loadImage() }
}

suspend fun loadWrongMethod() = coroutineScope {
    val supervisorJob = SupervisorJob(coroutineContext.job)
}

suspend fun loadImage(): String = coroutineScope {
    println("loadImage")
    delay(1000)
    return@coroutineScope "a"
}

