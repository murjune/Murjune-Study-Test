package com.murjune.practice.exception.supervisor

import com.murjune.practice.utils.launchWithName
import com.murjune.practice.utils.log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

// launchWithName("parent1", supervisorJob)
// 부모 Job : supervisorJob
// 현재 Job : launchWithName("parent1", supervisorJob) 으로 인해 만들어진 Job
// 따라서 parent1-child-child 예외가 parent1 까지 전파된다.
// parent1 이 예외에 의해 취소되므로, parent1-child2 는 취소가 전파됨

// 따라서, SuperVisorJob 이 아무런 도움이 되지 않는다. ㅜㅜ
// 즉, SupervisorJob 가 Job 계층 구조의 어떤 위치에 있어야하는지
// 이해하는 것이 중요하다.
private suspend fun ex1() = coroutineScope {
    val supervisorJob = SupervisorJob()
    launchWithName("parent1", supervisorJob) {
        launchWithName("parent1-child") {
            launchWithName("parent1-child-child") {
                error("error")
            }
            delay(100)
            log("parent1-child 실행중") // 예외 때문에 실행되지 않음
        }
        launchWithName("parent1-child2") {
            delay(100)
            log("parent1-child2 실행중") // 취소로 인해 실행되지 않음
        }
    }
    supervisorJob.complete()
    supervisorJob.join()
}

suspend fun main() {
    val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("예외 발생")
    }
    CoroutineScope(handler).launch {
        // Job() 팩토리 함수를 통해 job을 만들면 반드시 complete() 혹은 cancel()을 호출해줘야
        // 해당 Job() 이 끝난다(Complete or canceled)
        launch(handler) {
            error("s")
        }
        delay(10)
        println("a")
        supervisorScope {
            val job = async {
                delay(100)
                error("s")
                1
            }
//            try {
//                job.await()
//            }catch (e:Exception) {
//                println("await 에러 방지")
//            }
        }
        println("finish")
    }
    delay(1000)
}
