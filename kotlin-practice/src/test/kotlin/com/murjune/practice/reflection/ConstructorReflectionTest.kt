package com.murjune.practice.reflection

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure
import kotlin.test.Test

class ConstructorReflectionTest {
    interface Flyable {
        fun fly()
    }

    class Duck(val age: Int = 0) : Flyable {
        private var name: String = "오리"

        constructor(name: String) : this() {
            this.name = name
        }

        override fun fly() {
            println("Duck is flying")
        }
    }

    class Airplane : Flyable {
        override fun fly() {
            println("Airplane is flying")
        }
    }

    class Flyables(val airplane: Airplane, val duck: Duck)


    @Test
    fun `Duck 의 KClass 정보`() {
        val clazz = Duck::class
        assertSoftly {
            clazz.isAbstract.shouldBeFalse()
            clazz.java.isInterface.shouldBeFalse()
            clazz.isCompanion.shouldBeFalse()
            clazz.isData.shouldBeFalse()
            clazz.isFinal.shouldBeTrue()
            clazz.isInner.shouldBeFalse()
            clazz.isInstance(Duck()).shouldBeTrue()
            clazz.isSealed.shouldBeFalse()
        }
    }

    @Test
    fun `kotlin - 주 생성자로 Airplane 객체 생성`() {
        val clazz = Airplane::class

        // 주 생성자
        val primaryConstructor = clazz.primaryConstructor

        // 생성자 호출
        val airplane = primaryConstructor?.call()
        airplane.shouldBeInstanceOf<Airplane>()
    }

    @Test
    fun `kotlin - 주 생성자에 parameter 개수 만큼 인자를 넘기지 않으면 IllegalArgumentException 발생`() {
        val clazz = Duck::class

        // 주 생성자
        val primaryConstructor = clazz.primaryConstructor

        shouldThrow<IllegalArgumentException> {
            primaryConstructor?.call()
        }
    }

    @Test
    fun `kotlin - 주 생성자에 parameter 개수 만큼 인자를 call() 의 매개변수로 넘겨 객체 생성`() {
        val clazz = Duck::class

        // 주 생성자
        val primaryConstructor = clazz.primaryConstructor

        val airplane = primaryConstructor?.call(1)
        airplane.shouldBeInstanceOf<Duck>()
    }

    @Test
    fun `kotlin - 부 생성자로 Duck 객체 생성 후 이름 가져오기`() {
        val clazz = Duck::class

        // 주 생성자
        val constructors = clazz.constructors

        // 생성자 호출
        val duckConstructor: KFunction<Duck>? = constructors.firstOrNull {
            it.parameters.any { it.name == "name" }
        }
        val duck = duckConstructor?.call("Donald")
        val name = Duck::class.declaredMemberProperties.firstOrNull {
            it.name == "name"
        }
        duck.shouldNotBeNull()
        if (name is KProperty1) {
            name.isAccessible = true
            name.get(duck) shouldBe "Donald"
        }
    }

    @Test
    fun `중첩 클래스의 생성자 호출`() {
        val clazz = Flyables::class

        // 주 생성자
        val primaryConstructor = clazz.primaryConstructor
        val params =
            primaryConstructor?.parameters?.map { param ->
                param.type.jvmErasure.createInstance()
            }?.toTypedArray()
        // 생성자 호출
        params.shouldNotBeNull()
        val flyables = primaryConstructor.call(*params)
        flyables.shouldBeInstanceOf<Flyables>()
    }

    @Test
    fun `Flyable 에 해당하는 객체 생성하기`() {
        val clazz = Flyable::class

    }

    @Test
    fun `java constructor reflection example`() {
        val clazz = Airplane::class

        // 주 생성자
        val constructors = clazz.java
        println("Constructors: $constructors")

        // 생성자 호출
        val airplane = constructors.constructors[0].newInstance()
        airplane.shouldBeInstanceOf<Airplane>()
    }

    @Test
    fun `주 생성자가 없으면 primaryConstructor 는 Null을 반환한다`() {
        class NoPrimaryConstructor {
            constructor()
        }

        val clazz = NoPrimaryConstructor::class
        val primaryConstructor = clazz.primaryConstructor

        primaryConstructor.shouldBeNull()
    }

    interface Interface {}

    @Test
    fun `인터페이스의 primaryConstructor 는 Null을 반환한다`() {

        val clazz = Interface::class
        val primaryConstructor = clazz.primaryConstructor

        primaryConstructor.shouldBeNull()
    }

    object Singleton {}

    @Test
    fun `object 의 primaryConstructor 는 Null을 반환한다`() {

        val clazz = Singleton::class
        val primaryConstructor = clazz.primaryConstructor

        primaryConstructor.shouldBeNull()
    }

    @Test
    fun `abstract class 의 primaryConstructor 는 Null을 반환하지 않는다`() {
        abstract class AbstractClass {}

        val clazz = AbstractClass::class
        val primaryConstructor = clazz.primaryConstructor

        primaryConstructor.shouldNotBeNull()
    }

    @Test
    fun `abstract class 의 primaryConstructor 로 객체 생성 시 InstantiationException 발생`() {
        abstract class AbstractClass {}

        val clazz = AbstractClass::class
        val primaryConstructor = clazz.primaryConstructor

        shouldThrow<InstantiationException> {
            primaryConstructor?.call()
        }
    }
}