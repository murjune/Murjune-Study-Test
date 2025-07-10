package com.murjune.practice.android.libs.uri

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith

// ref: https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/tests/coretests/src/android/net/UriTest.java;drc=61197364367c9e404c7da6900658f1b16c42d0da;bpv=0;bpt=1;l=60
@RunWith(AndroidJUnit4::class)
class UriParseTest {

    @Test
    fun `올바른 문자열로 변환된다`() {
        val urlString = "https://example.com"
        val uri = Uri.parse(urlString)

        uri.toString() shouldBe urlString
    }

    @Test
    fun `스키마를 추출할 수 있다`() {
        val urlString = "https://example.com"
        val uri = Uri.parse(urlString)

        uri.scheme shouldBe "https"
    }

    @Test
    fun `authority를 추출할 수 있다`() {
        val urlString = "https://example.com:8080/path?param=value&foo=bar"
        val uri = Uri.parse(urlString)

        uri.authority shouldBe "example.com:8080"
    }

    @Test
    fun `포트를 추출할 수 있다`() {
        val urlString = "https://example.com:8080/path?param=value&foo=bar"
        val uri = Uri.parse(urlString)

        uri.port shouldBe 8080
    }

    @Test
    fun `호스트를 추출할 수 있다`() {
        val urlString = "https://example.com/path?param=value&foo=bar"
        val uri = Uri.parse(urlString)

        uri.host shouldBe "example.com"
    }

    @Test
    fun `경로를 추출할 수 있다`() {
        val urlString = "https://example.com/path?param=value&foo=bar"
        val uri = Uri.parse(urlString)

        uri.path shouldBe "/path"
    }

    @Test
    fun `마지막 경로 세그먼트를 추출할 수 있다`() {
        val urlString = "https://example.com/path/to/file.txt"
        val uri = Uri.parse(urlString)

        uri.lastPathSegment shouldBe "file.txt"
    }

    @Test
    fun `쿼리 파라미터를 추출할 수 있다`() {
        val urlString = "https://example.com/path?param=value&foo=bar"
        val uri = Uri.parse(urlString)

        uri.getQueryParameter("param") shouldBe "value"
        uri.getQueryParameter("foo") shouldBe "bar"
    }

    @Test
    fun `인코딩된 파라미터를 가져옴`() {
        val urlString = "https://example.com%20test/path"
        val uri = Uri.parse(urlString)

        uri.authority shouldBe "example.com test"
        uri.encodedAuthority shouldBe "example.com%20test" // 원본 authority
    }

    @Test
    fun `인코딩된 경로를 추출할 수 있다`() {
        val urlString = "https://example.com/path%20with%20spaces/file.txt"
        val uri = Uri.parse(urlString)

        uri.path shouldBe "/path with spaces/file.txt"
        uri.encodedPath shouldBe "/path%20with%20spaces/file.txt" // 원본 authority
    }

    @Test
    fun `잘못된 URI 문자열이 주어질 때 null 반환`() {
        val malformedUrl = "not-a-valid-uri"
        val uri = Uri.parse(malformedUrl)

        uri shouldNotBe null
        uri.scheme shouldBe null
        uri.host shouldBe null
        uri.path shouldBe malformedUrl
    }
}