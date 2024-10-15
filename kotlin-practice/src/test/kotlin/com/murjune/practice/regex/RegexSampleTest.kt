package com.murjune.practice.regex

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldNotMatch
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

class RegexSampleTest {
    @Test
    @DisplayName("\\는 특수문자를 그대로 사용할 때 사용")
    fun `공백을 제거한다`() {
        // given
        val target = " 123 45 "
        val regex = Regex("\\s")
        // when
        val result = target.replace(regex, "")
        // then
        val expected = "12345"
        result shouldBe expected
    }

    @Test
    fun `양쪽 공백을 제거한다`() {
        // given
        val target = " 123 45 "
        val regex = Regex("^\\s+|\\s+$")
        // when
        val result = target.replace(regex, "")
        // then
        val expected = "123 45"
        result shouldBe expected
    }

    @Test
    @DisplayName("4..12 글자 & 알파벳 대 소문자 & 숫자만 가능한 닉네임")
    fun `User 닉네임 유효성 체크`() {
        // given
        val nickname = "murjune"
        val nickname2 = "murjune123"
        val nickname3 = "murjune$1234"
        // when
        val regex = "^[a-zA-Z0-9]{4,12}$".toRegex()
        // then
        nickname shouldMatch regex
        nickname2 shouldMatch regex
        nickname3 shouldNotMatch regex.pattern
    }

    @Test
    fun `email 유효성 체크`() {
        // given
        val email = "sis9221302@gmail.com"
        val email2 = "murjune+odoong@gamil.com"
        val email3 = "오둥@naver.com"
        // when
        val regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$".toRegex()
        // then
        email shouldMatch regex
        email2 shouldMatch regex
        email3 shouldNotMatch regex.pattern
    }

    @Test
    fun `URL 유효성 체크`() {
        val url = "https://www.google.com"
        val url2 = "www.google.com"
        val url3 = "오둥.murjun.com"
        // when
        // http://, https://, ftp:// 로 시작하는지 확인 없을 수도 있음
        val startRegex = "^((http|https|ftp)://)?".toRegex()
        // 도메인 확인 www.google.com
        val domainRegex = "([a-zA-Z0-9]+\\.)+[a-zA-Z]{2,6}$".toRegex()
        // http://www.google.com
        val fullRegex = startRegex.pattern + domainRegex.pattern
        // then
        url shouldMatch fullRegex
        url2 shouldMatch fullRegex
        url3 shouldNotMatch fullRegex
    }

    @Test
    @DisplayName("순서는 강제 X - 영어, 숫자, 특수문자 조합 & 8자리 이상")
    fun `비밀번호 유효성 체크2`() {
        // given
        val validPassword = "Abc12345!"
        val invalidPassword1 = "1234"            // 숫자만, 8자리 미만
        val invalidPassword2 = "abcdefgh"        // 영어만, 숫자와 특수문자 없음
        val invalidPassword3 = "abc12345"        // 특수문자 없음
        val invalidPassword4 = "abc!@#$%"        // 숫자 없음

        // when
        val alphabetExist = "(?=.*[a-zA-Z])"
        val numberExist = "(?=.*[0-9])"
        val specialCharacterExist = "(?=.*[^a-zA-Z0-9])"
        val lengthOver8 = ".{8,}"
        val regex = "^$alphabetExist$numberExist$specialCharacterExist$lengthOver8$".toRegex()

        // then
        validPassword.matches(regex).shouldBeTrue()  // 유효한 비밀번호
        invalidPassword1.matches(regex).shouldBeFalse()  // 숫자만, 8자리 미만
        invalidPassword2.matches(regex).shouldBeFalse()  // 영어만, 숫자와 특수문자 없음
        invalidPassword3.matches(regex).shouldBeFalse()  // 특수문자 없음
        invalidPassword4.matches(regex).shouldBeFalse()  // 숫자 없음
    }

    @Test
    fun `1000 단위로 콤마를 찍는다`() {
        // given
        val money = 10000000
        val regex = Regex("(\\d)(?=(\\d{3})+$)")
        // when
        val result = money.toString().replace(regex, "$1,")
        // then
        val expected = "10,000,000"
        result shouldBe expected
    }

    @Test
    fun `특정 문자열이 1,000 원 패턴을 따르는지 확인`() {
        // given
        val money = "1,000,000 원"
        // when
        val regex = Regex("\\d+(,\\d{3})+ 원$")
        val result = money.matches(regex)
        // then
        result.shouldBeTrue()
    }

    @Test
    @DisplayName("000-0000-0000")
    fun `전화번호 검증`() {
        // given
        val phoneNum = "010-0123-2314"
        val phoneNum2 = "010-12-1234"
        // when
        val regex = "^\\d{3}-\\d{4}-\\d{4}$"
        // then
        phoneNum shouldMatch regex
        phoneNum2 shouldNotMatch regex
    }
}