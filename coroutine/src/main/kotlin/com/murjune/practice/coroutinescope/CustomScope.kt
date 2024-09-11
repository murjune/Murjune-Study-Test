package com.murjune.practice.coroutinescope

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun main() {
    val scope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext = Dispatchers.Default
    }

    scope.launch {
        delay(10)
        println(coroutineContext[Job])
        println(coroutineContext[Job]?.parent)
    }
    scope.cancel()
    delay(100)
}