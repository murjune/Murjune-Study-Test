package com.murjune.practice.exception.supervisor

import com.murjune.practice.utils.launchWithName
import com.murjune.practice.utils.log
import kotlinx.coroutines.*

// supervisorScope: SuperVisorJob + CoroutineScope
// 생성시 해당 함수를 호출한 코루틴의 Job을 부모로 가진 SupervisorJob을 생성한다.
// 그리고, 해당 Job 이 supervisorScope 의 Job 이 된다.
// supervisorScope {
//      부모 Job: 호출한 코루틴의 Job
//      현재 Job: supervisorScope 의 Job
// }
// 따라서, supervisorScope 를 사용하면 복잡한 Job 계층 구조를 만들지 않고도
// 예외 처리를 할 수 있다.

// 그리고, 자식 코루틴이 모두 실행 완료되면 자동으로 SupervisorJob 을 완료한다.
// ===> complete() 를 호출하지 않아도 된다.

private suspend fun preEx1() = coroutineScope {
    val parentJob = SupervisorJob(coroutineContext[Job])

    // 1-1) 자식 코루틴에게 supervisorJob 을 부모로 설정해줘야함
    launchWithName("parent1", parentJob) { // 여기까지만 예외 발생
        println("parentJob : ${coroutineContext[Job]?.parent}") // SupervisorJob
        launchWithName("Child A") {
            throw RuntimeException()
        }
        delay(100)
        log("Parent1 실행중") // 예외 때문에 실행되지 않음
    }
    // 1-2) 자식 코루틴에게 supervisorJob 을 부모로 설정해줘야함
    launchWithName("parent2", parentJob) {
        delay(100)
        log("parent2 실행중") // 실행됨
    }
    // 2) 명시적으로 완료시켜줘여함!
    parentJob.complete()
}

// 위와 동일한 코드이다
private suspend fun ex1() = coroutineScope {
    val rootJob = coroutineContext[Job]
    supervisorScope {
        println("parentJob == rootJob : ${coroutineContext[Job]?.parent == rootJob}") // rootJob
        println("job : ${coroutineContext[Job]}") // SupervisorCoroutine
        launchWithName("parent1") { // 여기 까지만 Exception 이 전파됨
            throw RuntimeException()
            // exception --> StandaloneCoroutine-handleJobException()
            //
            launchWithName("Child A") {
                throw RuntimeException()
            }
            delay(100)
            log("Parent1 실행중") // 예외 때문에 실행되지 않음
        }
        launchWithName("parent2") {
            delay(100)
            log("parent2 실행중") // 실행됨
        }
    }
    // coroutineScope 와 마찬가지로 supervisorScope 는 자식 코루틴이 모두 실행 완료되면
    // 자동으로 SupervisorJob 을 완료한다
    // + 해당 코루틴을 블록한다
    println("ex1--end")
}

private fun a3() = runBlocking {
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("${throwable.message} 처리")
    }

    val parentJob = launch(coroutineExceptionHandler) {
        supervisorScope {
            launchWithName("child") {
                error("error")
            }
        }
        delay(100)
        log("parent1 실행중")
    }
    launchWithName("parent2", parentJob) {
        delay(100)
        log("parent2 실행중")
    }
}

val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    log("${throwable.message} 처리")
}

private fun aasf44() = runBlocking(coroutineExceptionHandler) {
    launch {
        launch {
            coroutineScope {
                println(coroutineContext)
                launch {
                    error("error")
                }
            }
            println("end")
        }
    }
}

fun main() {
    aasf44()
    return
    runBlocking {
        ex1(); return@runBlocking
        log("parent context : $coroutineContext")
        CoroutineScope(SupervisorJob() + coroutineContext).launch {
            log("child context : $coroutineContext")
        }
        coroutineScope { }
        withContext(SupervisorJob()) {
            delay(100)
            log("child context : $coroutineContext")
        }
    }
}
