package com.murjune.practice.channelflow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.milliseconds

fun currentTime() = System.currentTimeMillis()
var startTime = 0L
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

private fun usersFlow(api: UserApi): Flow<User> = flow {
    var page = 0
    do {
        println("----Produce page $page----")
        val users = api.users(page++)
        println("$users - ${(currentTime() - startTime).milliseconds}")
        emitAll(users.asFlow())
    } while (users.isNotEmpty())
}

data class User(val name: String)

interface UserApi {
    suspend fun users(pageNumber: Int): List<User>
}

class FakeUserApi : UserApi {
    private val users = List(20) { User("User $it") }
    private val pageSize: Int = 3
    override suspend fun users(pageNumber: Int): List<User> {
        delay(1000)
        return users
            .drop(pageNumber * pageSize)
            .take(pageSize)
    }
}