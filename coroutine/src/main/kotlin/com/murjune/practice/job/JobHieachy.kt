package com.murjune.practice.job

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
//        ex3()
        val job = Job()
        println("root job: ${coroutineContext[Job]}")
        println("job: $job")
        val newScope = CoroutineScope(coroutineContext + job)
        // 새로운 scope 를 만들면 root job 이 현재 job 이 된다.
        // Job 이 newScope 의 job 이 된다.
        println("newScope Job: ${newScope.coroutineContext[Job]}")
        newScope.launch {
            // launch 를 하면 자식 job 이 생성된다.
            println("current job: ${coroutineContext[Job]}")
            // parent job 은 root job 이다.
            println("parent job: ${coroutineContext[Job]?.parent}")
        }
        val job2 = Job()
        delay(1)
        println("job2: $job2")
        newScope.launch(job2) {
            // launch 를 통해 새로운 job 을 만들면 현재 job 이 바뀐다.
            println("current job: ${coroutineContext[Job]}")
            // launch 를 하면 job2 가 parent job 이 된다.
            println("parent job: ${coroutineContext[Job]?.parent}")
        }
    }
}
