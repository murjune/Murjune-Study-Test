package com.murjune.practice.suspendcoroutine

import com.murjune.practice.utils.log
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface Callback {
    fun onSuccess()
    fun onFail()
}

class Context {
    var callback: Callback? = null

    fun registerCallback(callback: Callback) {
        this.callback = callback
        executeSomething()
    }

    fun executeSomething() {
        thread {
            // do SomeThing
        }
    }
}

suspend fun main() {
    val context = Context()
    suspendCancellableCoroutine<Unit> { c ->
        // 1. start
        val callback = object : Callback {
            override fun onSuccess() {
                c.resume(Unit) // 3. complete
            }

            override fun onFail() {
                c.resumeWithException(IllegalArgumentException("dd"))
            }
        }
        context.registerCallback(callback) // block

        if (c.isCompleted) {
            log("completed")
        }
        c.invokeOnCancellation {
            // close
        }
    }
}
