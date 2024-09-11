package com.murjune.practice.exception.supervisor

import com.murjune.practice.utils.launchWithName
import com.murjune.practice.utils.log
import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext


// 코루틴은 큰 작업을 작은 작업으로 나누는 divide and conquer 방식을 사용한다.
// 그런데, 예외 전파를 제한하기 위해 Job 을 사용했다
// 이 방식은 부모-자식의 구조화를 깨기 때문에
// 부모가 자식에게 cancel 을 전파하지 못한다.

// 그럼 어떻게 해야할까?
// 이때 사용하는 것이 SupervisorJob 이다.

// SupervisorJob 은 부모-자식의 계층 구조를 유지하면서
// 예외 전파를 제한할 수 있다.

// SupervisorJob 은 자식 코루틴으로 부터 예외를 전파받지 않는 특수한 Job 이다.
// 만약, 일반 Job 은 자식 코루틴에서 예외 발생시 부모 코루틴으로 예외를 전파받아
// 취소되자만, SupervisorJob 은 자식 코루틴에서 예외를 전파받지 않아 취소 X

// 따라서, 부모 코루틴은 자식 코루틴의 예외에 영향을 받지 않고 계속 실행된다.

// 해당 예시는 새로운 SupervisorJob 을 새로운 부모 Job 으로 오버라이딩하여 사용한다.
// parent1-child 코루틴에서 예외가 발생해도 parent1 까지만 예외가 전파되고
// parent2 는 정상적으로 실행된다.

// 하지만, 이 또한 root 코루틴과의 구조화를 깨버린 것이기 때문에
// root 코루틴이 취소되면 parent1, parent2 는 취소되지 않는다.
// 즉 root 코루틴에 cancel() 을 호출해도 parent1, parent2 는 계속 실행된다.
private suspend fun ex1() = coroutineScope {
    val parentJob = SupervisorJob()
    launchWithName("parent1", parentJob) {
        launchWithName("parent1-child") {
            error("error")
        }
        delay(100)
        log("parent1 실행중")
    }
    launchWithName("parent2", parentJob) {
        delay(100L)
        log("parent2 실행중")
    }
    parentJob.complete()
    parentJob.join()
}

// 따라서, 코루틴의 구조화를 유지하면서 SupervisorJob 을 사용하려면
// SupervisorJob() 의 인자로 부모 Job 을 전달해야한다.
// 전달 안하면 SupervisorJob 은 root Job 이 된다.
// 참고) 코루틴 컨텍스트는 부모와 자식 Job 을 가지고 있다.
//  val children = this.coroutineContext[Job]?.children
//  val job = this.coroutineContext[Job]
private suspend fun ex2() = coroutineScope {
//    val parentJob = SupervisorJob(parent = coroutineContext[Job])
    val parentJob = SupervisorJob()
    launchWithName("parent1", parentJob) {
        launchWithName("parent1-child") {
            error("error")
        }
        delay(100)
        log("parent1 실행중")
    }
    launchWithName("parent2", parentJob) {
        delay(100L)
        log("parent2 실행중")
    }
//    parentJob.complete()// SupervisorJob 도 CompletableJob 이다.
//    parentJob.join()
    parentJob.complete() // 이거 실행 안하면 join 을 걸면 해당 job 이 complete 가 안돼 안끝남
    parentJob.join() // 이거 없으면 root 가 job 을 기다리지 않아서 끝나버림
    log("root 코루틴 끝")
}

private suspend fun ex3() = coroutineScope {
    // parentJob 의 부모를 root 코루틴으로 설정
    val parentJob = SupervisorJob(parent = coroutineContext[Job])
    launchWithName("parent1", parentJob) {
        launchWithName("parent1-child") {
            error("error")
        }
        delay(100)
        log("parent1 실행중") // 예외 때문에 실행되지 않음
    }
    launchWithName("parent2", parentJob) {
        delay(100L)
        log("parent2 실행중")
    }
    SupervisorJob()
    parentJob.complete()
    // join 안해도 된다. 왜냐하면 parentJob 의 부모가 root 이기 때문에
    log("root 코루틴 끝")
}

private suspend fun foo() = suspend { delay(1) }

suspend fun main() {
    val ex = CoroutineExceptionHandler { _, ex ->
        println("에러")
    }

    CoroutineScope(ex).launch {}.invokeOnCompletion { throw CancellationException() }

    delay(1000)
}