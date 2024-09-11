package com.murjune.practice.reflection

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberExtensionFunctions
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberExtensionFunctions
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.staticFunctions
import kotlin.reflect.jvm.isAccessible
import kotlin.test.Test

class FunctionReflectionTest {

    fun sum(x: Int, y: Int) = x + y

    @Test
    fun `KFunction call, invoke`() {
        val kFunction: KFunction<Int> = ::sum
        kFunction.shouldNotBeNull()
        shouldThrow<IllegalArgumentException> {
            //2개의 인자를 받는 함수에 3개의 인자를 넘기면 KotlinReflectionInternalError 발생
            kFunction.call(1, 2, 3)
        }
        kFunction.call(1, 2) shouldBe 3
        val kFunction2 = ::sum// KFunction2<Int, Int, Int> 타입
        kFunction2.invoke(1, 2) shouldBe 3 // KFunction 의 인자와 반환값을 안다면 invoke 함수를 시용하자!
    }

    class Person {
        fun greeting() {}
        private fun fullName() {}
        private fun Int.isAdult() {}

        companion object {
            fun instance(): Person = Person()

            @JvmStatic
            fun instance2(): Person = Person()
        }
    }

    @Test
    fun `super class 와 class 에 선언된 함수`() {
        // super class : Any, sub class : Person
        val clazz = Person::class
        // function = memberFunction + extensionFunction
        val functions = clazz.functions
        val memberFunctions = clazz.memberFunctions
        val extensionFunctions = clazz.memberExtensionFunctions
        // [greeting, fullName, isAdult, toString, hashCode, equals]
        functions.size shouldBe 6
        // [greeting, fullName, toString, hashCode, equals]
        memberFunctions.size shouldBe 5
        // [isAdult]
        extensionFunctions.size shouldBe 1
    }

    @Test
    fun `class 에 선언된 함수`() {
        val clazz = Person::class
        val memberFunctions = clazz.declaredFunctions
        val declaredFunctions = clazz.declaredMemberFunctions
        val extensionFunctions = clazz.declaredMemberExtensionFunctions
        // [greeting, fullName, isAdult]
        memberFunctions.size shouldBe 3
        // [greeting, fullName, isAdult]
        declaredFunctions.size shouldBe 2
        // []
        extensionFunctions.size shouldBe 1
    }

    @Test
    fun `class 에 선언된 public 함수만 가져오기`() {
        val clazz = Person::class
        val memberFunctions = clazz.declaredFunctions
        val publicFunctions = memberFunctions.filter { it.visibility == KVisibility.PUBLIC }
        // [greeting]
        publicFunctions.size shouldBe 1
    }

    @Test
    fun `static function 호출하기`() {
        val clazz = Person::class
        val instance = clazz.staticFunctions
        instance.size shouldBe 0
    }

    @Test
    fun `companion object function 호출하기`() {
        val clazz = Person::class
        val instance = clazz.companionObject?.declaredMemberFunctions
        instance?.size shouldBe 2
    }

    @Test
    fun `private function 호출하기`() {
        class Foo {
            private fun bar() = "bar"
        }

        val clazz = Foo::class
        val barProperty = clazz.declaredMemberFunctions.find { it.name == "bar" }
        if (barProperty is KFunction) {
            barProperty.isAccessible = true
            barProperty.call(Foo()) shouldBe "bar"
        }
    }
}