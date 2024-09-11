package com.murjune.practice.dispatcher

import com.murjune.practice.utils.launchWithName
import com.murjune.practice.utils.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlin.time.measureTime

@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun ex_singleThreadpool1() = coroutineScope {
    val dispatcher = newFixedThreadPoolContext(1, "multiple-thread")
    val scope = CoroutineScope(coroutineContext + dispatcher)
    scope.launch {
        repeat(1000) {
            print("-")
        }
    }
    scope.launch {
        repeat(1000) {
            print("|")
        }
    }
    // 결과는 ----------- 가 다 끝나고 |||||||||| 가 실행된다.
    // 이유는 1번이 끝날때까지 2번 코루틴은 작업 대기 상태에 놓이기 때문이다.
}

@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun ex_singleThreadpool2() = coroutineScope {
    measureTime {
        val job = Job()
        val dispatcher = newSingleThreadContext("single-thread")
        val scope = CoroutineScope(dispatcher + job)
        scope.launchWithName("coroutine-1") {
            log("ccccc")
            delay(1000)
            log("ddddd")
        }
        scope.launchWithName("coroutine-2") {
            log("ccccc")
            delay(1000)
            log("ddddd")
        }
        job.complete()
        job.join()
    }.also { log("실행 시간: $it") }
}

@OptIn(DelicateCoroutinesApi::class)
private suspend fun ex_multipleThreadpool() = coroutineScope {
    measureTime {
        val job = Job()
        val dispatcher = newFixedThreadPoolContext(2, "multiple-thread")
        val scope = CoroutineScope(dispatcher + job)
        scope.launchWithName("coroutine-1") {
            log("aaaaa")
            delay(1000)
            log("bbbbb")
        }
        scope.launchWithName("coroutine-2") {
            log("ccccc")
            delay(1000)
            log("ddddd")
        }
        job.complete()
        job.join()
    }.also { log("실행 시간: $it") }
}

fun main() {
    runBlocking {
        val dispatcher = newFixedThreadPoolContext(1, "multiple-thread")
        val scope = CoroutineScope(coroutineContext + dispatcher)
        scope.launch {
            repeat(1000) {
                print("-")
            }
        }
        scope.launch {
            repeat(1000) {
                print("|")
            }
        }
    }
}
