package com.murjune.practice.job

import com.murjune.practice.utils.launchWithName
import com.murjune.practice.utils.log
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

// 새로운 잡으로 부모를 바꿔서 coroutineScope 는 자식이 없음
// 따라서, 바로 실행이 종료된다.
private suspend fun ex1() = coroutineScope {
    val job = Job()
    launchWithName("1", job) {
        delay(100)
        log("1 - start")
    }
    launchWithName("2", job) {
        delay(100)
        log("2 - start")
    }
}

// 해당 함수를 빌드하면 해당 코루틴은 끝나질 않는다.
// 왤까??
// Job 은 body 값이 없어 complete 가 되질 않는다.
// 그런데 join 함수는 job이 complete 될 때까지 기다린다.
// 따라서, job 이 complete 되지 않아 join 이 끝나지 않는다.
private suspend fun ex2() = coroutineScope {
    val job = Job()
    launchWithName("1", job) {
        delay(100)
        log("1 - start")
    }
    launchWithName("2", job) {
        delay(100)
        log("2 - start")
    }
    job.join()
}

// 자식들만 join 하면 된다.
private suspend fun ex3() = coroutineScope {
    val job = Job()
    launchWithName("1", job) {
        delay(100)
        log("1 - start")
    }
    launchWithName("2", job) {
        delay(100)
        log("2 - start")
    }


    log("job complete")
}

// 또 다른 방법으로는 completion 을 사용하여 명시적으로
// 자식 코루틴이 끝나면 job 이 끝나도록 할 수 있다.

// Complete()
// Job 을 명시적으로 완료합니다.
// 작업이 완료되면 결과는 참이고, 그렇지 않으면 (이미 완료된 경우) 거짓을 반환합니다.
// 2번 이상 호출시 아무런 효과가 없으며 항상 false를 반환
// 이 함수는 작업이 아직 완료되지 않았거나 취소되지 않은 경우 이 작업을 완료 상태로 전환합니다.
// 그러나 이 작업에 "자식이 있는 경우에는 완료 상태로 전환하고 모든 자식이 완료되면 완료 상태가 됩니다."

// 그니까 자식이 있으면 자식이 끝날 때까지 기다린다.
// 본인은 바로 끝난다.
private suspend fun ex4() = coroutineScope {
    val job = Job().apply {
        invokeOnCompletion {
            log("job complete")
        }
    }
    launchWithName("1", job) {
        delay(100)
        log("1 - start")
    }
    launchWithName("2", job) {
        delay(100)
        log("2 - start")
    }
    println("job is Completed: ${job.complete()}") // true
    println("job is Completed: ${job.complete()}") // false
    println("job is Completed: ${job.complete()}") // false
    job.join() // join 을 걸지 않으면 root 가 바로 종료된다.
    println("Will be printed")
}

suspend fun main() {
    runBlocking {
        ex4()
        delay(1000)
    }
}
