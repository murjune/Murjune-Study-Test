package com.murjune.practice.exception.supervisor

import com.murjune.practice.utils.launchWithName
import com.murjune.practice.utils.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

// SuperVisorJob + CoroutineScope
// 만약, CoroutineScope 의 CoroutineContext에 SupervisorJob 이 설정되면
// CoroutineScope 의 모든 자식 코루틴은 SupervisorJob 의 자식이 된다.
// 즉 Parent 1 과 Parent 2는 이제 예외에 한해서 서로 독립적이다.

private suspend fun ex1() = coroutineScope {
    val parentJob = SupervisorJob(coroutineContext[Job])
    val coroutineScope = CoroutineScope(parentJob)
    coroutineScope.launchWithName("parent1") {
        launchWithName("Child A") {
            throw RuntimeException()
        }
        delay(100)
        log("Parent1 실행중") // 예외 때문에 실행되지 않음
    }
    coroutineScope.launchWithName("parent2") {
        delay(100)
        log("parent2 실행중") // 실행됨
    }
    parentJob.complete()
}

fun main() {
    runBlocking {
        ex1()
    }
}
