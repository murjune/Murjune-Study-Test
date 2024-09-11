package com.murjune.practice.suspense

import com.murjune.practice.utils.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun main() = runBlocking {
    println(count6())
}

private suspend fun count6(): Int = withContext(Dispatchers.Default) {
    var count = 0
    runBlocking {
        log("runBlocking start")
        coroutineScope {
            repeat(1000) {
                launch {
                    log("runBlocking start $it")
                    count++
                }
            }
        }
    }
    count
}
