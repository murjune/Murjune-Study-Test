package com.murjune.practice.exception.supervisor


import com.murjune.practice.utils.launchWithName
import com.murjune.practice.utils.log
import kotlinx.coroutines.*

// 예외 발생
private suspend fun ex1() = coroutineScope {
    launchWithName("parent1") {
        launchWithName("parent1-child") {
            error("error")
        }
        delay(100)
        log("parent1 실행중")
    }
    launchWithName("parent2") {
        delay(100L)
        log("parent2 실행중")
    }
    delay(1000)
}

// Job 을 사용한 예외 전파 제한
// 코루틴은 자신의 부모 코루틴으로만 예외를 전파한다.
// 따라서, 부모 코루틴과의 계층 구조를 끊어주는 Job 을 사용하면 예외를 전파하지 않는다.
// 참고) launch 함수에 새로운 Job 을 전달하면 새로운 Job 을 부모 Job 으로 사용한다.

// 따라서, parent1, parent2 둘다 실행되는 것을 볼 수 있다
private suspend fun ex2() = coroutineScope {
    val parentJob = launchWithName("parent1") {
        // 새로운 Job 을 생성하여 부모 Job 에 추가
        launchWithName("parent1-child", Job()) {
            error("error")
        }
        delay(100)
        log("parent1 실행중")
    }
    launchWithName("parent2", parentJob) {
        delay(100L)
        log("parent2 실행중")
    }
    delay(1000)
}

// 새로운 잡을 추가해 부모-자식 관계를 끊어주면 하나의 문제가 생긴다.
// 바로, cancel 이 이제 더 이상 자식 코루틴까지 전파되지 않는다는 것이다.
// 이는 문제가 될 수 있는데, 만약 여러개의 이미지 다운로드해야하는 작업이 있을 때,
// 하나의 사진마다 하나의 자식 코루틴이 담당 하고 있다고 해보자
// 이떄 사용자가 다운로드 취소를 하면 모든 자식 코루틴을 취소해야하는데,
// 새로운 Job을 추가해 부모-자식 관계를 끊어주면 이러한 작업을 할 수 없다.

// 이미지3 다운로드 작업은 더이상 root 의 자식이 아니기 때문에 cancel 이 되지 않는다.
// 따라서, 취소해도 이미지 3 다운로드 작업은 계속 진행된다.

// 뿐만 아니라, 새로운 Job 이 생겨 root 는 job이 끝날 때까지 기다리지 않는다.

private suspend fun uploadImage(imagePath: String): String = withContext(Dispatchers.IO) {
    delay(100) // 로컬 이미지를 불러와 Form 데이터 형태로 바꾸는 작업
    if (imagePath == "이미지 4") error("예외 발생 😵")
    val imageUrl = "서버 이미지: $imagePath"
    imageUrl
}


suspend fun uploadImages(localImagePaths: List<String>): List<String?> = coroutineScope {
    val supervisor = SupervisorJob(coroutineContext.job)
    val result = localImagePaths.map { localImagePath ->
        async(supervisor) { uploadImage(localImagePath) }
    }.map {
        try {
            it.await()
        } catch (e: IllegalStateException) {
            null
        }
    }
    supervisor.complete()
    result
}

private suspend fun foo() = coroutineScope {
    val supervisor = SupervisorJob(coroutineContext.job)
    // 잘못된 예외 전파 방식 1
    CoroutineScope(supervisor).launch {
        launch { error("에러") }
    }
    // 잘못된 예외 전파 방식 2
    launch(supervisor) {
        launch { error("에러") }
    }
}

fun main() = runBlocking {
    val localImagePaths = listOf("이미지 1", "이미지 2", "이미지 3", "이미지 4")
    val images = uploadImages(localImagePaths)
    println(images)
}
//        try {
//            coroutineScope {
//                val deferred = async { error("에러") }
//                delay(10)
//                try {
//                    deferred.await()
//                } catch (e: Exception) {
//                    println("잡음")
//                }
//            }
//        } catch (e: Exception) {
//            println("잡았다능")
//        }
//        println(1)