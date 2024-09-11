package com.murjune.practice.reflection

import io.kotest.matchers.shouldBe
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.typeOf
import kotlin.test.Test

class KTypeTest {
    class A(val a: Int)

    @Test
    fun `typeOf - Int KType 검사`() {
        val firstProperty = A::class.declaredMemberProperties.first()
        val kType = firstProperty.returnType
        kType shouldBe typeOf<Int>()
    }

    @Test
    fun `createType - Int KType 검사`() {
        val firstProperty = A::class.declaredMemberProperties.first()
        val kType = firstProperty.returnType
        kType shouldBe 1::class.createType()
    }

    @Test
    fun `classifier 검사`() {
        val firstProperty = A::class.declaredMemberProperties.first()
        val kType = firstProperty.returnType
        kType.classifier shouldBe Int::class
    }
}