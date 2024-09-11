package com.murjune.practice.document

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory

class StudyDocumentTest {
    private lateinit var document: Document

    @BeforeEach
    fun setUp() {
        document =
            DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse("src/test/kotlin/com/murjune/practice/document/ex_file.xml")
    }

    @Test
    fun `basic test`() = runTest {
        1 shouldBe 1
    }

    @Test
    fun `Document test`() {
        val items = document.getElementsByTagName("item")
        items.length shouldBe 10
    }

    @Test
    fun `read channel test`() {
        val items = document.getElementsByTagName("channel")
        val element = items.item(0) as Element
        element.tagName shouldBe "channel"
    }

    @Test
    fun `read channel title test`() {
        val items = document.getElementsByTagName("item")
        val element = items.item(0) as Element
        val title = element.getElementsByTagName("title").item(0).textContent
        title shouldBe "우리팀은 카프카를 어떻게 사용하고 있을까"
    }

    @Test
    fun `read 찻번쩨 item 읽어오기 test`() {
        val items = document.getElementsByTagName("item")
        val element = items.item(0) as Element
        val postTitle = element.getElementsByTagName("title").item(0).textContent
        val link = element.getElementsByTagName("link").item(0).textContent
        val pubDate = element.getElementsByTagName("pubDate").item(0).textContent
        val title = items.item(0).parentNode.nodeName
        println(title)
        postTitle shouldBe "우리팀은 카프카를 어떻게 사용하고 있을까"
        link shouldBe "https://techblog.woowahan.com/17386/"
        pubDate shouldBe "Thu, 30 May 2024 06:40:10 +0000"
    }

    @Test
    fun `parse Date`() {
        val dateString = "Thu, 23 May 2024 06:50:25 +0000"

        val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
        val dateTime = LocalDateTime.parse(dateString, formatter)
        dateTime shouldBe LocalDateTime.of(2024, 5, 23, 6, 50, 25)
    }
}
