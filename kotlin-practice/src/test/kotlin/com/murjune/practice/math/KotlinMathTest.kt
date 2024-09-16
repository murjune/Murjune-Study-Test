package com.murjune.practice.math

import io.kotest.matchers.shouldBe
import java.math.RoundingMode
import kotlin.math.acos
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.test.Test

class KotlinMathTest {
    @Test
    fun `test`() {
        1 shouldBe 1
    }

    @Test
    fun `소수점 1번째 자리에서 반올림`() {
        round(1.5) shouldBe 2.0
        round(1.4) shouldBe 1.0
        round(1.4999) shouldBe 1.0
    }

    @Test
    fun `소수점 1번째 자리에서 올림`() {
        ceil(1.5) shouldBe 2.0
        ceil(1.4) shouldBe 2.0
        ceil(1.01) shouldBe 2.0
    }

    @Test
    fun `소수점 1번째 자리에서 버림`() {
        floor(1.5) shouldBe 1.0
        floor(1.4) shouldBe 1.0
        floor(1.99) shouldBe 1.0
    }

    @Test
    fun `소수점 2번째 짜리까지 구하기`() {
        val num = 1.2345
        val result = String.format("%.2f", num).toDouble()
        result shouldBe 1.23
    }

    @Test
    fun `소수점 3번째 짜리에서 반올림`() {
        val num = 1.235.toBigDecimal()
        val rounded = num.setScale(2, RoundingMode.HALF_UP).toDouble()
        rounded shouldBe 1.24
    }

    @Test
    fun `sqrt - 두 점 사이 거리 구하기`() {
        val x1 = 1
        val y1 = 1
        val x2 = 4
        val y2 = 5
        // sqrt : 제곱근
        // pow : 거듭제곱
        val distance = sqrt((x2 - x1).toDouble().pow(2) + (y2 - y1).toDouble().pow(2))
        distance shouldBe 5.0
    }

    @Test
    fun `hypot - 두 점 사이 거리 구하기`() {
        val x1 = 1
        val y1 = 1
        val x2 = 4
        val y2 = 5
        // hypot : 두 점 사이의 거리를 구하는 함수
        val distance = hypot((x2 - x1).toDouble(), (y2 - y1).toDouble())
        distance shouldBe 5.0
    }

    @Test
    fun `삼각형의 각 구하기`() {
        val a = 3.0
        val b = 4.0
        val c = 5.0
        // cosA = (b^2 + c^2 - a^2) / 2bc
        val cosA = (b.pow(2) + c.pow(2) - a.pow(2)) / (2 * b * c)
        val cosB = (a.pow(2) + c.pow(2) - b.pow(2)) / (2 * a * c)
        val cosC = (a.pow(2) + b.pow(2) - c.pow(2)) / (2 * a * b)
        // acos : 역코사인 - 라디안 값
        val radianA = acos(cosA)
        val radianB = acos(cosB)
        val radianC = acos(cosC)
        // toDegrees : 라디안 값을 각도(도)로 변환
        val degreeA = Math.toDegrees(radianA).toInt()
        val degreeB = Math.toDegrees(radianB).toInt()
        val degreeC = Math.toDegrees(radianC).toInt()
        degreeA shouldBe 36
        degreeB shouldBe 53
        degreeC shouldBe 90
    }
}