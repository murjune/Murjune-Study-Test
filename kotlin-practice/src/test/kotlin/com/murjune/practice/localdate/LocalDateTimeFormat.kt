package com.murjune.practice.localdate

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class LocalDateTimeFormat {
    /**
     * DateTimeFormatter.ofPattern() 을 사용하면 출력형식 customizing 가능
     * yyyy: 년도
     * MM: 달
     * dd: 일
     * ss: 초
     * SSS: 밀리세컨드
     * ofPattern 인자로 "yyyy-MM-dd HH:mm:ss:SSS" 와 같이 패턴을 넣어주면 된다.
     * */
    @Test
    @DisplayName("LocalDateTime -> yyyy.mm.dd HH:mm:ss")
    fun testCustomFormat() {
        //given
        val localDateTime = LocalDateTime.of(2024, 4, 16, 10, 0, 0)
        val expect = "2024.04.16 10:00:00"
        //when
        val pattern = "yyyy.MM.dd HH:mm:ss"
        val actual = localDateTime.format(DateTimeFormatter.ofPattern(pattern))
        //then
        actual shouldBe expect
    }

    /**
     * 1) LocalDateTime 의 toString() 을 사용하면 기본적으로 ISO_LOCAL_DATE_TIME 형식으로 변환
     * 2) ISO_LOCAL_DATE_TIME: yyyy-mm-ddThh:mm:ss
     * 3) ISO_LOCAL_DATE: yyyy-mm-dd
     * 4) ISO_LOCAL_TIME: hh:mm:ss
     * 5) ISO_DATE: yyyy-mm-dd
     * 6) ISO_TIME: hh:mm:ss
     * 7) ISO_DATE_TIME: yyyy-mm-ddThh:mm:ss
     * */
    @Test
    @DisplayName("LocalDateTime -> ISO Local Date (yyyy-mm-dd)")
    fun `test`() {
        // given
        val dt = LocalDateTime.of(2024, 4, 15, 21, 0, 0)
        // when
        val toString = dt.toLocalDate().toString() // 기본적으로 ISO_LOCAL_DATE 형식으로 변환
        val time = dt.format(DateTimeFormatter.ISO_TIME)
        val date = dt.format(DateTimeFormatter.ISO_DATE)
        val dateTime = dt.format(DateTimeFormatter.ISO_DATE_TIME)
        val localTime = dt.format(DateTimeFormatter.ISO_LOCAL_TIME)
        val localDate = dt.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val localDateTime = dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        // then
        toString shouldBe "2024-04-15"
        time shouldBe "21:00:00"
        date shouldBe "2024-04-15"
        dateTime shouldBe "2024-04-15T21:00:00"
        localTime shouldBe "21:00:00"
        localDate shouldBe "2024-04-15"
        localDateTime shouldBe "2024-04-15T21:00:00"
    }

    /**
     * 1) LocalDateTime 의 toString() 을 사용하면 기본적으로 ISO_LOCAL_DATE_TIME 형식으로 변환
     * 2) ISO_LOCAL_DATE_TIME: yyyy-mm-ddThh:mm:ss
     * 3) ISO_LOCAL_DATE: yyyy-mm-dd
     * 4) ISO_LOCAL_TIME: hh:mm:ss
     * 5) ISO_DATE: yyyy-mm-dd
     * 6) ISO_TIME: hh:mm:ss
     * 7) ISO_DATE_TIME: yyyy-mm-ddThh:mm:ss
     * */
    @Test
    @DisplayName("LocalDateTime -> ISO Local Date (yyyy-mm-dd)")
    fun `test2`() {
        // given
        val dt = LocalDateTime.of(2024, 4, 15, 21, 0, 0).atZone(
            ZoneId.of("UTC")
        )
        // when
        val toString = dt.toLocalDate().toString() // 기본적으로 ISO_LOCAL_DATE 형식으로 변환
        val time = dt.format(DateTimeFormatter.ISO_TIME)
        val date = dt.format(DateTimeFormatter.ISO_DATE)
        val dateTime = dt.format(DateTimeFormatter.ISO_DATE_TIME)
        val localTime = dt.format(DateTimeFormatter.ISO_LOCAL_TIME)
        val localDate = dt.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val localDateTime = dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        // then
        assertSoftly {
            toString shouldBe "2024-04-15"
            time shouldBe "21:00:00"
            date shouldBe "2024-04-15"
            dateTime shouldBe "2024-04-15T21:00:00"
            localTime shouldBe "21:00:00"
            localDate shouldBe "2024-04-15"
            localDateTime shouldBe "2024-04-15T21:00:00"
        }
    }
}
