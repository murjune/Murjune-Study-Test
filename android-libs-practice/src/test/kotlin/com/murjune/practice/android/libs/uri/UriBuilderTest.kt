package com.murjune.practice.android.libs.uri

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.Test
import org.junit.runner.RunWith

/**
 * URI 생성과 수정을 위한 학습 테스트
 * 
 * 📌 핵심 개념
 * - Uri.Builder(): 처음부터 URI를 단계별로 구성
 * - uri.buildUpon(): 기존 URI를 기반으로 수정
 * - 자동 인코딩: 특수문자가 자동으로 URL 인코딩됨
 */
@RunWith(AndroidJUnit4::class)
class UriBuilderTest {

    @Test
    fun `Uri_Builder()로 처음부터 URI를 구성할 수 있다`() {
        // Given: 각 구성 요소를 하나씩 추가
        val uri = Uri.Builder()
            .scheme("https")
            .authority("api.example.com")
            .path("/v1/users")
            .appendQueryParameter("page", "1")
            .appendQueryParameter("size", "10")
            .build()

        // Then: 올바른 URI 문자열이 생성됨
        uri.toString() shouldBe "https://api.example.com/v1/users?page=1&size=10"
        uri.scheme shouldBe "https"
        uri.authority shouldBe "api.example.com"
        uri.path shouldBe "/v1/users"
        uri.getQueryParameter("page") shouldBe "1"
        uri.getQueryParameter("size") shouldBe "10"
    }

    @Test
    fun `appendPath()로 경로를 단계별로 추가할 수 있다`() {
        // Given: 경로를 단계별로 추가
        val uri = Uri.Builder()
            .scheme("https")
            .authority("api.example.com")
            .path("/v1") // 기본 경로 설정
            .appendPath("vip") // 경로 추가
            .appendPath("users") // 경로 추가
            .build()

        // Then: 경로가 올바르게 연결됨
        uri.toString() shouldBe "https://api.example.com/v1/vip/users"
        uri.path shouldBe "/v1/vip/users"
        uri.pathSegments shouldBe listOf("v1", "vip", "users")
        uri.lastPathSegment shouldBe "users"
    }

    @Test
    fun `특수문자가 포함된 쿼리 파라미터가 자동으로 인코딩된다`() {
        // Given: 특수문자가 포함된 쿼리 값
        val queryWithSpecialChars = "Hello World & More"
        val uri = Uri.Builder()
            .scheme("https")
            .authority("example.com")
            .appendQueryParameter("message", queryWithSpecialChars)
            .build()

        // Then: 특수문자가 자동으로 인코딩됨
        uri.toString() shouldContain "message=Hello%20World%20%26%20More"
        // 하지만 getQueryParameter()로 가져올 때는 디코딩됨
        uri.getQueryParameter("message") shouldBe "Hello World & More"
    }

    @Test
    fun `buildUpon()으로 기존 URI에 쿼리 파라미터를 추가할 수 있다`() {
        // Given: 기존 URI
        val originalUri = Uri.parse("https://api.example.com/v1/users?page=1")
        
        // When: buildUpon()으로 쿼리 파라미터 추가
        val modifiedUri = originalUri.buildUpon()
            .appendQueryParameter("size", "20")
            .appendQueryParameter("sort", "created_at,desc")
            .build()

        // Then: 기존 파라미터는 유지되고 새 파라미터가 추가됨
        modifiedUri.toString() shouldBe "https://api.example.com/v1/users?page=1&size=20&sort=created_at%2Cdesc"
        modifiedUri.getQueryParameter("page") shouldBe "1" // 기존 파라미터 유지
        modifiedUri.getQueryParameter("size") shouldBe "20" // 새 파라미터 추가
        modifiedUri.getQueryParameter("sort") shouldBe "created_at,desc" // 새 파라미터 추가
    }

    @Test
    fun `buildUpon()으로 기존 URI의 경로를 변경할 수 있다`() {
        // Given: 기존 URI
        val originalUri = Uri.parse("https://api.example.com/v1/users")
        
        // When: buildUpon()으로 경로 변경
        val modifiedUri = originalUri.buildUpon()
            .path("/v2/customers") // 경로 완전 교체
            .appendQueryParameter("filter", "active")
            .build()

        // Then: 경로가 변경되고 쿼리 파라미터가 추가됨
        modifiedUri.toString() shouldBe "https://api.example.com/v2/customers?filter=active"
        modifiedUri.path shouldBe "/v2/customers"
        modifiedUri.getQueryParameter("filter") shouldBe "active"
    }

    @Test
    fun `buildUpon()으로 기존 URI의 호스트를 변경할 수 있다`() {
        // Given: 기존 URI
        val originalUri = Uri.parse("https://api.example.com/v1/users?page=1")
        
        // When: buildUpon()으로 호스트 변경
        val modifiedUri = originalUri.buildUpon()
            .authority("api.newdomain.com")
            .build()

        // Then: 호스트가 변경되고 나머지는 유지됨
        modifiedUri.toString() shouldBe "https://api.newdomain.com/v1/users?page=1"
        modifiedUri.authority shouldBe "api.newdomain.com"
        modifiedUri.getQueryParameter("page") shouldBe "1" // 기존 쿼리 파라미터 유지
    }
}