package com.murjune.practice.utils

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun CoroutineScope.launchWithName(name: String, job: Job? = null, block: suspend CoroutineScope.() -> Unit): Job {
    val newJob = if (job == null) {
        CoroutineName(name)
    } else {
        CoroutineName(name) + job
    }
    return launch(newJob) {
        log("$name 시작")
        block()
    }.apply {
        invokeOnCompletion {
            log("$name 종료")
        }
    }

}
