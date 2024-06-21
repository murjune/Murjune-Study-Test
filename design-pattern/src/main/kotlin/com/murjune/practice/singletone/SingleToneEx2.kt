package designpattern.singletone

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import kotlin.concurrent.thread

class SingleToneEx2 private constructor() {
    companion object {
        private var instance: SingleToneEx2? = null

        @OptIn(InternalCoroutinesApi::class)
        fun instance() =
            instance ?: synchronized(this) {
                instance ?: SingleToneEx2().also {
                    instance = it
                }
            }

        @OptIn(InternalCoroutinesApi::class)
        fun getInstance() =
            instance?.run {
                // println("만들어진 객체를 불러옵니다")
            } ?: synchronized(this) {
                instance ?: SingleToneEx2().also {
                    println("초기화")
                    instance = it
                }
            }
    }
}

fun main() {
    val threads =
        List(1000000) {
            thread {
                SingleToneEx2.getInstance()
            }
        }
    val threads2 =
        List(1000000) {
            thread {
                SingleToneEx2.getInstance()
            }
        }

    threads.forEach { it.join() }
    threads2.forEach { it.join() }
}
