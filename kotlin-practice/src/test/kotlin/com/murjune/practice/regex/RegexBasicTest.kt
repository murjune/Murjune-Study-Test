package com.murjune.practice.regex

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.string.shouldNotMatch
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

/**
 * 정규식 문법에 대해 공부 해보자!
 *
 * + : 바로 앞 패턴이 1번 이상 반복됨
 * . : 아무 문자 한 개와 매칭
 * * : 바로 앞 패턴이 0번 이상 반복됨
 * ^ : 문자열의 시작과 매칭
 * $ : 문자열의 끝과 매칭
 * {} : 반복 횟수를 지정 (예: {3}은 3번 반복)
 * [] : 대괄호 안에 있는 문자들 중 하나와 매칭
 * () : 그룹을 지정
 * ? : 바로 앞 패턴이 0번 또는 1번 나타남 (선택적)
 * - : 범위를 지정할 때 사용 (예: [a-z])
 * \\ : 특수 문자를 문자 그대로 사용하기 위해 이스케이프할 때 사용
 *
 * (?=...): 긍정형 전방탐색 - ...에 해당하는 문자열이 뒤에 나오는지 확인 (캡쳐는 안함)
 * (?!...): 부정형 전방탐색 - ...에 해당하는 문자열이 뒤에 나오지 않는지 확인 (캡쳐는 안함)
 *
 * */
class RegexBasicTest {

    @Test
    @DisplayName("a+b+c 는 a, b, c가 순서대로 나오는 패턴")
    fun `+ 는 앞 패턴이 1번 이상 반복`() {
        // given
        val s = "abc"
        val s2 = "aabbc"
        val s3 = "aabbcc"
        val s4 = "abcd"
        val pattern = "a+b+c"
        // when & then
        s shouldMatch pattern
        s2 shouldMatch pattern
        s3 shouldNotMatch pattern
        s4 shouldNotMatch pattern
    }

    @Test
    @DisplayName(".* 는 뒤에 문자가 뭐든 상관 없다")
    fun `a 가 0번 이상 반복되는 패턴`() {
        // given
        val s = ""
        val s2 = "a"
        val s3 = "aa"
        val s4 = "abc"
        val pattern = "a.*"
        // when & then
        s shouldNotMatch pattern
        s2 shouldMatch pattern
        s3 shouldMatch pattern
        s4 shouldMatch pattern
    }

    @Test
    @DisplayName("^는 문자열의 시작을 나타냄")
    fun `^a 는 a 시작하는 패턴`() {
        // given
        val s = "abcdbd"
        val s2 = "bc"
        val pattern = "^a.*"
        // when & then

        s shouldMatch pattern
        s2 shouldNotMatch pattern
    }

    @Test
    @DisplayName("$ 는 문자열의 끝을 나타냄")
    fun `d$ 는 d로 끝나는 패턴`() {
        // given
        val s = "abcdbd"
        val s2 = "bc"
        val pattern = ".*d$"
        // when & then
        s shouldMatch pattern
        s2 shouldNotMatch pattern
    }

    @Test
    @DisplayName("{2} 는 2번 반복되는 패턴")
    fun `a 가 2번 반복되는 패턴`() {
        // given
        val s = "a"
        val s2 = "aa"
        val s3 = "aaa"

        val pattern = "a{2}"
        // when & then
        s shouldNotMatch pattern
        s2 shouldMatch pattern
        s3 shouldNotMatch pattern
    }

    @Test
    @DisplayName("{2,5} 는 2번에서 5번 반복되는 패턴")
    fun `a 가 2번에서 5번 반복되는 패턴`() {
        // given
        val s = "a"
        val s2 = "aa"
        val s3 = "aaaaa"
        val s4 = "aaaaaa"

        val pattern = "a{2,5}"
        // when & then
        s shouldNotMatch pattern
        s2 shouldMatch pattern
        s3 shouldMatch pattern
        s4 shouldNotMatch pattern
    }

    @Test
    @DisplayName("[] 안에 있는 문자 중 하나와 매치되는 패턴")
    fun `알파벳에 해당하는 패턴`() {
        // given
        val s = "odoong"
        val s2 = "122"
        val s3 = "오둥e"
        // when
        val pattern = "[a-zA-z]+"
        // then
        s shouldMatch pattern
        s2 shouldNotMatch pattern
        s3 shouldNotMatch pattern
    }

    @Test
    @DisplayName("() 으로 패턴 그룹을 지정할 수 있다")
    fun `odoong 이 2번 반복되는 패턴`() {
        // given
        val s = "odoongodoong"
        val s2 = "odoongodoongodoong"
        // when
        val pattern = "(odoong){2}"
        // then
        s shouldMatch pattern
        s2 shouldNotMatch pattern
    }

    @Test
    @DisplayName("? 는 0 또는 1번 반복되는 패턴")
    fun `a 가 0 또는 1번 반복되는 패턴`() {
        // given
        val s = ""
        val s2 = "a"
        val s3 = "aaa"
        val pattern = "a?"
        // when & then
        s shouldMatch pattern
        s2 shouldMatch pattern
        s3 shouldNotMatch pattern
    }

    @Test
    @DisplayName("| 는 or bit 연산자")
    fun `a 또는 b 패턴`() {
        // given
        val s = "a"
        val s2 = "b"
        val s3 = "c"

        val pattern = "a|b"
        // when & then
        s shouldMatch pattern
        s2 shouldMatch pattern
        s3 shouldNotMatch pattern
    }

    @Test
    @DisplayName("\\s 는 공백 패턴")
    fun `공백 패턴`() {
        // given
        val s = "1"
        val s2 = ""
        val s3 = " "
        val s4 = "\t"
        val s5 = "\n"

        val pattern = "\\s"
        // when & then
        s shouldNotMatch pattern
        s2 shouldNotMatch pattern
        s3 shouldMatch pattern
        s4 shouldMatch pattern
        s5 shouldMatch pattern
    }

    @Test
    @DisplayName("\\d 는 숫자 패턴")
    fun `숫자 패턴`() {
        // given
        val s = "1"
        val s2 = "a"
        val s3 = "!"

        val pattern = "\\d"
        // when & then
        s shouldMatch pattern
        s2 shouldNotMatch pattern
        s3 shouldNotMatch pattern
    }

    @Test
    @DisplayName("(?=...): 긍정형 전방탐색 - ...에 해당하는 문자열이 뒤에 나오는지 확인")
    fun `긍정형 전방탐색`() {
        // given
        val s = "a111"
        val s2 = "a112"
        val s3 = "a112"
        // when
        val pattern = "a(?=1{3})"
        val regex = pattern.toRegex()
        // then
        s shouldContain regex
        s2 shouldNotContain regex
        s3 shouldNotContain regex
    }

    @Test
    @DisplayName("(?=...) : ...에 해당하는 문자열은 캡쳐는 안함")
    fun `?= 의 앞에 문자열만 캡쳐한다`() {
        // given
        val s = "a32a1"
        // when
        val pattern = "a(?=1)"
        val regex = pattern.toRegex()
        // then
        val result = regex.find(s)?.groupValues.orEmpty()
        result shouldBe listOf("a")
    }

    @Test
    @DisplayName("(?!...): 부정형 전방탐색 - ...에 해당하는 문자열이 뒤에 나오지 않는지 확인")
    fun `부정형 전방탐색`() {
        // given
        val s = "a111"
        val s2 = "a211"
        val s3 = "a311"
        // when
        val regex = "a(?!1{3})".toRegex()
        // then
        assertSoftly {
            s shouldNotContain regex
            s2 shouldContain regex
            s3 shouldContain regex
        }
    }
}