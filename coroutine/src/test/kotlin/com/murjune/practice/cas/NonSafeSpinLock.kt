package com.murjune.practice.cas

class NonSafeSpinLock {
    @Volatile
    private var isLock = false

    fun lock() {
        while (true) {
            if (!isLock) { // 락 획득
                isLock = true
                println("락 획득")
                break
            }

            println("락 획득 실패, 스핀 대기..")
        }
    }

    fun unLock() {
        isLock = false
    }
}