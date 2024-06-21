package designpattern.singletone

import kotlin.concurrent.thread

class SingleToneEx private constructor() {
    companion object {
        private var instance: SingleToneEx? = null

        fun getInstance() =
            instance?.let {
                println("만들어진 객체를 불러옵니다")
                instance
            } ?: run {
                println("초기화")
                instance = SingleToneEx()
                instance
            }
    }
}

fun main() {
    val threads =
        List(100) {
            thread {
                val singleToneEx = SingleToneEx.getInstance()
            }
        }

    threads.forEach { it.join() }
}
