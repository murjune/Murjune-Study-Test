package com.murjune.practice.android.libs.uri

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UriBuilderTest {

    @Test
    fun `복잡한 URI를 단계별로 구성`() {
        val uri = Uri.Builder()
            .scheme("https")
            .authority("api.example.com")
            .path("/v1") // "/"를 넣어도 안넣어도 된다
            .appendPath("vip")
            .appendPath("users")
            .appendQueryParameter("page", "1")
            .appendQueryParameter("size", "10")
            .appendQueryParameter("sort", "name,asc")
            .build()

        // 자동 인코딩 처리됨
        uri.toString() shouldBe "https://api.example.com/v1/vip/users?page=1&size=10&sort=name%2Casc"
        uri.lastPathSegment shouldBe "users"
        uri.pathSegments shouldBe listOf("v1", "vip", "users")
        uri.getQueryParameter("page") shouldBe "1"
        uri.getQueryParameter("size") shouldBe "10"
        uri.getQueryParameter("sort") shouldBe "name,asc"
    }

    @Test
    fun `인코딩이 필요한 문자열을 자동으로 처리된다`() {
        val queryWithSpecialChars = "Hello World & More"
        val uri = Uri.Builder()
            .scheme("https")
            .authority("example.com")
            .appendQueryParameter("message", queryWithSpecialChars)
            .build()

        uri.toString() shouldContain "message=Hello%20World%20%26%20More"
        uri.getQueryParameter("message") shouldBe "Hello World & More"
    }

    @Test
    fun `buildUpon 으로 기존 URI를 수정할 수 있다`() {
        val originalUri = Uri.parse("https://api.example.com/v1/users?page=1")
        
        val modifiedUri = originalUri.buildUpon()
            .appendQueryParameter("size", "20")
            .appendQueryParameter("sort", "created_at,desc")
            .build()

        modifiedUri.toString() shouldBe "https://api.example.com/v1/users?page=1&size=20&sort=created_at%2Cdesc"
        modifiedUri.getQueryParameter("page") shouldBe "1"
        modifiedUri.getQueryParameter("size") shouldBe "20"
        modifiedUri.getQueryParameter("sort") shouldBe "created_at,desc"
    }

    @Test
    fun `buildUpon으로 기존 URI의 경로를 변경할 수 있다`() {
        val originalUri = Uri.parse("https://api.example.com/v1/users")
        
        val modifiedUri = originalUri.buildUpon()
            .path("/v2/customers") // path 교체
            .appendQueryParameter("filter", "active")
            .build()

        modifiedUri.toString() shouldBe "https://api.example.com/v2/customers?filter=active"
        modifiedUri.path shouldBe "/v2/customers"
        modifiedUri.getQueryParameter("filter") shouldBe "active"
    }

    @Test
    fun `buildUpon 으로 기존 URI의 호스트를 변경할 수 있다`() {
        val originalUri = Uri.parse("https://api.example.com/v1/users?page=1")
        
        val modifiedUri = originalUri.buildUpon()
            .authority("api.newdomain.com")
            .build()

        modifiedUri.toString() shouldBe "https://api.newdomain.com/v1/users?page=1"
        modifiedUri.authority shouldBe "api.newdomain.com"
        modifiedUri.getQueryParameter("page") shouldBe "1"
    }
}