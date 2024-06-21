package com.murjune.practice.sequence

import com.murjune.practice.utils.faker
import kotlin.test.Test
import kotlin.time.measureTime

class SequenceSpeedTest {
    @Test
    fun `test`() {
        // given
        val largeUsers = largeUsers(10000)
        val collectionTime = measureTime {
            largeUsers.filter {
                it.age in (20..50)
            }.filter {
                it.name.first() in ('A'..'F')
            }.map {
                it.copy(age = it.age + 20)
            }
        }
        val sequenceTime = measureTime {
            largeUsers.asSequence().filter {
                it.age in (20..50)
            }.filter {
                it.name.first() in ('A'..'F')
            }.map {
                it.copy(age = it.age + 20)
            }.toList()
        }
        println("collectionTime $collectionTime")
        println("sequenceTime $sequenceTime")
    }

    private fun largeUsers(size: Int): List<User> {
        return List(size) {
            createUser()
        }
    }

    data class User(val name: String, val age: Int)

    fun createUser(
        name: String = faker.lorem().characters(),
        age: Int = faker.number().numberBetween(15, 100)
    ): User {
        return User(name, age)
    }
}
