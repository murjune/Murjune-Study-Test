package com.murjune.practice.channelflow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.milliseconds

suspend fun main() {
    val api = FakeUserApi()
    startTime = currentTime()
    val user = usersFlow(api).first {
        println("Collect $it ${(currentTime() - startTime).milliseconds}")
        delay(1000)
        it.name == "User 3"
    }
    println(user)
}

private fun usersFlow(api: UserApi): Flow<User> = channelFlow {
    var page = 0
    do {
        println("----Produce page $page----")
        val users = api.users(page++)
        println("$users - ${(currentTime() - startTime).milliseconds}")
        users.forEach { send(it) }
    } while (users.isNotEmpty())
}