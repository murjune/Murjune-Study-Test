package com.murjune.practice.delegate

import io.kotest.matchers.shouldBe
import kotlin.reflect.KProperty
import kotlin.test.Test

class DelegatePropertyTest {
    class NameDelegate(private var name: String) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return name
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            name = value
        }
    }

    fun name(name: String): NameDelegate {
        return NameDelegate(name)
    }

    data class Person(val name: String, val age: Int)

    class PersonDelegate(private val name: String, private val age: Int) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Person {
            return Person(name, age)
        }
    }

    fun person(name: String, age: Int): PersonDelegate {
        return PersonDelegate(name, age)
    }

    @Test
    fun `MutableProperty Delegate getter`() {
        val nameDelegate: String by name("Alice")
        nameDelegate shouldBe "Alice"
    }

    @Test
    fun `MutableProperty Delegate setter`() {
        var nameDelegate: String by name("Alice")
        nameDelegate = "Bob"
        nameDelegate shouldBe "Bob"
    }

    @Test
    fun `Property Delegate getter`() {
        val personDelegate: Person by person("Alice", 29)
        personDelegate shouldBe Person("Alice", 29)
    }
}