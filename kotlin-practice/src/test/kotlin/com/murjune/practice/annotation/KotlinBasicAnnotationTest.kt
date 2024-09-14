package com.murjune.practice.annotation

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.test.Test

class KotlinBasicAnnotationTest {
    annotation class All

    @Target(AnnotationTarget.PROPERTY)
    annotation class PropertyOnly

    @Target(AnnotationTarget.CLASS)
    annotation class ClassOnly

    @All
    @ClassOnly
    class Pizza(@PropertyOnly val topping: String)

    // 컴파일 이후 정보가 사라짐
    @Retention(AnnotationRetention.SOURCE)
    annotation class Source

    // 런타임에는 정보가 사라짐, Reflection으로 접근 불가
    @Retention(AnnotationRetention.BINARY)
    annotation class Binary

    // Reflection으로 접근 가능
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Runtime

    @Source
    @Binary
    @Runtime
    class Chicken

    annotation class Burger

    class Person {
        @Burger
        val chicken = Chicken()
    }

    @Test
    fun `어노테이션 동등성 비교`() {
        Person::chicken.findAnnotation<Burger>() shouldBe Burger()
        Burger() shouldBe Burger()
    }

    @Test
    fun `어노테이션 동일성 비교`() {
        Person::chicken.findAnnotation<Burger>() shouldNotBeSameInstanceAs Burger()
        Burger() shouldNotBeSameInstanceAs Burger()
    }

    @Test
    fun `클래스의 어노테이션 존재 여부`() {
        // given
        val pizzaClass = Pizza::class
        // when
        val hasClassAnnotation = pizzaClass.hasAnnotation<All>()
        val hasAllAnnotation = pizzaClass.hasAnnotation<ClassOnly>()
        // then
        hasClassAnnotation.shouldBeTrue()
        hasAllAnnotation.shouldBeTrue()
    }

    @Test
    fun `프로퍼티의 어노테이션 존재 여부`() {
        // given
        val pizzaTopping = Pizza("올리브")::topping
        // when
        val hasPropertyAnnotation = pizzaTopping.hasAnnotation<PropertyOnly>()
        // then
        hasPropertyAnnotation.shouldBeTrue()
    }

    @Test
    fun `어노테이션의 Reflection 접근 가능 여부`() {
        // given
        val chickenClass = Chicken::class
        // when
        val hasSourceAnnotation = chickenClass.hasAnnotation<Source>()
        val hasBinaryAnnotation = chickenClass.hasAnnotation<Binary>()
        val hasRuntimeAnnotation = chickenClass.hasAnnotation<Runtime>()
        // then
        hasSourceAnnotation.shouldBeFalse() // SOURCE는 리플렉션에서 접근 불가
        hasBinaryAnnotation.shouldBeFalse() // BINARY는 리플렉션에서 접근 불가
        hasRuntimeAnnotation.shouldBeTrue() // 성공
    }
}