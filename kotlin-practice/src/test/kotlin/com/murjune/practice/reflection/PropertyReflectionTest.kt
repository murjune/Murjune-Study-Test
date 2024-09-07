package com.murjune.practice.reflection

import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.test.Test

class PropertyReflectionTest {
    // 리플렉션 : 런타임에 클래스의 정보를 얻어내거나 수정하는 기능
    // 코틀린 리플랙션을 사용하려면 kotlin-reflect 라이브러리를 추가해야 한다.
    // implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")

    @Test
    fun `클래스의 모든 property 가져오기`() {
        class Person(val name: String, val age: Int)

        val kClass: KClass<Person> = Person::class
        kClass.simpleName shouldBe "Person"
        val memberNames = kClass.declaredMemberProperties.map { it.name }
        println(kClass.declaredMemberProperties)
        memberNames shouldBe listOf("age", "name")
    }

    @Test
    fun `super class 와 sub class 의 property 가져오기`() {
        open class Super(val superName: String)
        class Sub(val subName: String) : Super("super")

        val kClass: KClass<Sub> = Sub::class
        val memberNames = kClass.memberProperties.map { it.name }
        memberNames shouldBe listOf("subName", "superName")
    }

    @Test
    fun `public val 값 읽기`() {
        class Person(val name: String, var age: Int)

        val person = Person("Alice", 29)
        // 지역변수는 member Reference 사용 못함
        val nameProperty: KProperty1<Person, String> = Person::name
        nameProperty.get(person) shouldBe "Alice"
    }

    @Test
    fun `public val 값 변경 - java reflection 활용 kotlin은 불가능`() {
        class Person(val name: String, var age: Int)

        val person = Person("Alice", 29)
//         kProperty.set(person, "Bob") // kotlin은 불가능
        val field = Person::class.java.getDeclaredField("name")
        field.isAccessible = true
        field.set(person, "Bob")
        person.name shouldBe "Bob"
    }

    @Test
    fun `public var 값 읽고-변경`() {
        class Person(val name: String, var age: Int)

        val person = Person("Alice", 29)
        val kProperty: KMutableProperty1<Person, Int> = Person::age
        kProperty.get(person) shouldBe 29
        kProperty.set(person, 30)
        person.age shouldBe 30
    }

    @Test
    fun `private val 프로퍼티 값 읽기`() {
        class Foo {
            private val bar = "bar"
        }

        val clazz = Foo::class
        val barProperty = clazz.declaredMemberProperties.find { it.name == "bar" }
        if (barProperty is KProperty1) {
            barProperty.isAccessible = true // private property 접근 가능하게 설정
            barProperty.get(Foo()) shouldBe "bar"
            barProperty.getter.call(Foo()) shouldBe "bar"
        }
    }

    @Test
    fun `private var 프로퍼티 값 읽고-변경`() {
        class Foo {
            private var bar = "bar"
        }

        val foo = Foo()
        val clazz = Foo::class
        val barProperty = clazz.declaredMemberProperties.find { it.name == "bar" }
        if (barProperty is KMutableProperty1) {
            val typeParameters = barProperty.returnType.arguments
            println(typeParameters)
            barProperty.isAccessible = true
            barProperty.setter.call(foo, "newBar") // newBar로 변경
            barProperty.get(foo) shouldBe "newBar"
        }
    }
}