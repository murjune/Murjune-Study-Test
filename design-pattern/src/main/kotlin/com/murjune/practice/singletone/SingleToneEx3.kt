package designpattern.singletone

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import kotlin.concurrent.thread

class SingleToneEx3 private constructor() {
    companion object {
        @Volatile
        private var instance: SingleToneEx3? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getInstance() =
            instance?.let {
                println("만들어진 객체를 불러옵니다")
                instance
            } ?: synchronized(this) {
                println("초기화")
                instance = SingleToneEx3()
                instance
            }
    }
}

fun main() {
//    val d : String by lazy {  }
    val threads =
        List(100) {
            thread {
                val singleToneEx = SingleToneEx3.getInstance()
            }
        }

    threads.forEach { it.join() }
}
