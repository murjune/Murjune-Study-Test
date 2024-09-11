package com.murjune.practice.dispatcher

import com.murjune.practice.utils.log
import kotlinx.coroutines.*

fun main() {
    runBlocking {
        val threadNames = mutableSetOf<String>()
        threadNames.add(Thread.currentThread().name)
        withContext(Dispatchers.Default.limitedParallelism(5)) {
            val jobs = List(1000) {
                launch {
                    threadNames.add(Thread.currentThread().name)
                    delay(100)
                }
            }
            jobs.joinAll()
        }

        val uniqueThreadsCount = threadNames.size
        log(
            "Unique Threads: $uniqueThreadsCount PROCESSOR ${
                Runtime.getRuntime().availableProcessors()
            } \nTHREAD NAME \n${threadNames.joinToString("\n")}"
        )
    }
}