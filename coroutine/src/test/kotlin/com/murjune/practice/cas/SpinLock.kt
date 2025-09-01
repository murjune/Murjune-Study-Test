package com.murjune.practice.cas

import java.util.concurrent.atomic.AtomicBoolean

// CAS(Compare And Swap) 기반 스핀락 구현
// 참고). TAS (Test And Set) 방식과 같이 다른 방식으로도 스핀락을 구현할 수 있음
class SpinLock {
    private val isLock = AtomicBoolean(false)

    fun lock() {
        while (true) {
            if (isLock.compareAndSet(false, true)) { // 락 획득
                println("락 획득")
                break
            }

            println("락 획득 실패, 스핀 대기..")
        }
    }

    fun unLock() {
        isLock.set(false)
    }
}