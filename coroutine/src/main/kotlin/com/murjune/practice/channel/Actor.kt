package com.murjune.practice.channel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ObsoleteCoroutinesApi::class)
suspend fun main() {
    coroutineScope {
        var count = 0
        val actor = actor<Int>(capacity = 10) {
            val receiveChannel: ReceiveChannel<Int> = channel
            for (i in receiveChannel) {
                count += i
            }
            println(count)
        }
        withContext(Dispatchers.Default) {
            List(1000) {
                launch {
                    delay(1)
                    actor.send(1)
                }
            }
            coroutineContext[Job]?.children?.forEach { it.join() }
            actor.close()
        }
    }
    coroutineScope {
        var count = 0
        val channel = produce<Int>(capacity = 10) {
            for (i in 1..1000) {
                send(1)
            }
        }
        launch(Dispatchers.Default) {
            for (i in channel) {
                count += i
            }
            println(count)
        }
    }
}