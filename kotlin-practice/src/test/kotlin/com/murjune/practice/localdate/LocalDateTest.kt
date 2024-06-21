package com.murjune.practice.localdate


import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class LocalDateTest {
    @Test
    fun `DateTime 동등성 동일성 테스트`() {
        val dt1 = LocalDateTime.of(2022, 4, 1, 0, 0, 0)
        val dt2 = LocalDateTime.of(2022, 4, 1, 0, 0, 0)
        dt1 shouldBe dt2
        dt1 shouldNotBeSameInstanceAs dt2
    }

    @Test
    @DisplayName("yyyy.mm.dd 를 LocalDate 로 parse")
    fun test() {
        // given
        val date = "2024.04.15"
        val expect = LocalDate.of(2024, 4, 15)
        // when
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val localDate = LocalDate.parse(date, formatter)
        // then
        localDate shouldBe expect
    }

    @Test
    @DisplayName("2022.04.01 을 LocalDate 로 변환")
    fun test1() {
        // given
        val date = "2022.04.01"
        val timezone = "UTC"
        // when
//        val localDate = LocalDate.parse(date)
//        val localDateWithTimezone =
//            LocalDate.parse(date).atStartOfDay().toInstant(ZoneOffset.of(timezone)).toLocalDate()
        Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        println(LocalDateTime.now())
        println(LocalDateTime.now(ZoneId.of("UTC")))
        println(LocalDateTime.now(ZoneOffset.of(ZoneOffset.UTC.id)))
    }
}

