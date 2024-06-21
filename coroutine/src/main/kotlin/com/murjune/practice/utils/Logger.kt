package com.murjune.practice.utils

// vm 옵션에 -Dkotlinx.coroutines.debug 주면 어떤 코루틴이 어떤 스레드에서 실행되는지 확인 가능
fun log(msg: String, hasTime: Boolean = false) {
    println(buildString {
        append("[")
        append(Thread.currentThread().name)
        append("] - ")
        append(msg)
        if (hasTime) {
            append(" - ")
            append(System.currentTimeMillis().toString().takeLast(5))
            append(" ")
        }
    })
}

//long pid = ProcessHandle.current().pid();
//System.out.println("Current PID: " + pid);
