package com.murjune.practice.sequence

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SequenceStudyTest : FunSpec({

    test("generateSequence - 기본 사용법: 초기값과 다음 값 생성 함수") {
        // 1부터 시작해서 1씩 증가하는 시퀀스
        val sequence = generateSequence(1) { it + 1 }
        val result = sequence.take(5).toList()

        result shouldBe listOf(1, 2, 3, 4, 5)
    }

    test("generateSequence - 조건부 종료: null 반환 시 시퀀스 종료") {
        // 1부터 10까지만 생성
        val sequence = generateSequence(1) { current ->
            if (current < 10) current + 1 else null
        }
        val result = sequence.toList()

        result shouldBe listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    }

    test("generateSequence - 무한 시퀀스와 takeWhile") {
        // 무한 시퀀스를 생성하고 조건으로 제한
        val sequence = generateSequence(0) { it + 2 } // 짝수 무한 시퀀스
        val result = sequence.takeWhile { it < 10 }.toList()

        result shouldBe listOf(0, 2, 4, 6, 8)
    }

    test("generateSequence - 피보나치 수열") {
        // Pair를 사용한 피보나치 수열 생성
        val fibonacci = generateSequence(Pair(0, 1)) { (a, b) ->
            Pair(b, a + b)
        }.map { it.first }

        val result = fibonacci.take(10).toList()

        result shouldBe listOf(0, 1, 1, 2, 3, 5, 8, 13, 21, 34)
    }

    test("generateSequence - seedFunction 사용: 초기값도 함수로 생성") {
        var counter = 0
        val sequence = generateSequence(
            seedFunction = {
                counter++
                if (counter <= 3) counter else null
            },
        ) { current ->
            counter++
            if (counter <= 3) counter else null
        }

        val result = sequence.toList()

        result shouldBe listOf(1, 2, 3)
    }

    test("generateSequence - 2의 거듭제곱") {
        val powersOfTwo = generateSequence(1) { it * 2 }
        val result = powersOfTwo.take(8).toList()

        result shouldBe listOf(1, 2, 4, 8, 16, 32, 64, 128)
    }

    test("generateSequence - 실용 예시: 파일 경로의 부모 디렉토리 탐색") {
        val path = "/home/user/documents/project/src/main"

        // 경로를 역순으로 탐색
        val directories = generateSequence(path) { currentPath ->
            val lastSlash = currentPath.lastIndexOf('/')
            if (lastSlash > 0) currentPath.take(lastSlash) else null
        }.toList()

        directories shouldBe listOf(
            "/home/user/documents/project/src/main",
            "/home/user/documents/project/src",
            "/home/user/documents/project",
            "/home/user/documents",
            "/home/user",
            "/home",
        )
    }

    test("generateSequence - lazy evaluation: 필요한 만큼만 계산") {
        var callCount = 0
        val sequence = generateSequence(1) {
            callCount++
            it + 1
        }

        // take(3)만 호출하므로 next 함수는 3번만 실행됨
        sequence.take(3).toList()

        callCount shouldBe 2 // 초기값 1 다음에 2, 3을 생성하기 위해 2번 호출
    }

    test("generateSequence - 실용 예시: 페이지네이션") {
        data class Page(val number: Int, val hasNext: Boolean)

        // 페이지 1부터 시작해서 hasNext가 true인 동안 계속
        val pages = generateSequence(Page(1, true)) { current ->
            if (current.hasNext && current.number < 5) {
                Page(current.number + 1, current.number < 4)
            } else {
                null
            }
        }

        val pageNumbers = pages.map { it.number }.toList()

        pageNumbers shouldBe listOf(1, 2, 3, 4, 5)
    }

    test("generateSequence - take(n)이 실제 시퀀스 크기보다 클 때") {
        // 10개만 생성되는 시퀀스
        val sequence = generateSequence(1) { current ->
            if (current < 10) current + 1 else null
        }

        // 12개를 요청하지만 실제로는 10개만 반환됨
        val result = sequence.take(12).toList()

        result shouldBe listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        result.size shouldBe 10 // take(12)를 호출했지만 10개만 반환
    }
})