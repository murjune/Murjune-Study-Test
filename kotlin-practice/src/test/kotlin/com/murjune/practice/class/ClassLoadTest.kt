package com.murjune.practice.`class`

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ClassLoadTest {

    @BeforeEach
    fun setUp() {
        reset()
    }

    @Test
    fun `object 는 최초 호출 시 초기화 된다 - 즉, 지연 초기화 된다`() {
        isInnerObjectInitialized.shouldBeFalse()
        // when
        Outer.InnerObject
        // then
        isInnerObjectInitialized.shouldBeTrue()
    }

    @Test
    fun `Outer 가 초기화될 때, Companion object 도 초기화된다`() {
        Outer()
        // then
        isOuterInitialized.shouldBeTrue()
        isCompanionObjectInitialized.shouldBeTrue()
    }

    @Test
    fun `Nested class 가 초기화되도, Outer class 의 Companion object 는 초기화되지 않는다`() {
        // when
        Outer.NestedClass()
        // then
        isCompanionObjectInitialized.shouldBeFalse()
        isNestedClassInitialized.shouldBeTrue()
    }

    companion object Counter {
        var isOuterInitialized = false
        var isInnerObjectInitialized = false
        var isCompanionObjectInitialized = false
        var isInnerClassInitialized = false
        var isNestedClassInitialized = false

        fun reset() {
            isOuterInitialized = false
            isInnerObjectInitialized = false
            isCompanionObjectInitialized = false
            isInnerClassInitialized = false
            isNestedClassInitialized = false
        }
    }
}

private class Outer {
    init {
        ClassLoadTest.isOuterInitialized = true
    }

    object InnerObject {
        init {
            ClassLoadTest.isInnerObjectInitialized = true
        }
    }

    companion object Factory {
        init {
            ClassLoadTest.isCompanionObjectInitialized = true
        }
    }

    class NestedClass {
        init {
            ClassLoadTest.isNestedClassInitialized = true
        }
    }
}