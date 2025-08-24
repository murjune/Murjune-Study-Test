package com.murjune.practice.android.libs.uri

import android.content.UriMatcher
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith


/**
 * UriMatcher 학습 테스트
 *
 * 📌 UriMatcher란?
 * ContentProvider에서 받은 URI가 어떤 종류의 요청인지 구분하기 위한 도구입니다.
 *
 * 📌 주요 개념
 * - URI 패턴을 숫자 코드로 매핑
 * - # : 숫자 와일드카드 (예: /books/# → /books/123)
 * - "*" : 문자열 와일드카드 (예: /books/"*" → /books/fiction)
 * - [UriMatcher.NO_MATCH] : 매칭되지 않는 URI의 기본값
 *
 * 📌 실무 활용
 * ContentProvider에서 query(), insert(), update(), delete() 메서드에서
 * URI 종류에 따라 다른 동작을 수행할 때 사용합니다.
 * */
@RunWith(AndroidJUnit4::class)
class UriMatcherTest {

    @Test
    fun `UriMatcher의 기본 동작을 이해한다`() {
        // Given: UriMatcher 초기화 (기본값은 NO_MATCH)
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        val uri = Uri.Builder()
            .scheme("content")
            .authority("com.example.app.provider")
            .path("books")
            .build()

        // When: 아직 패턴을 등록하지 않은 상태에서 URI 매칭 시도
        val result = uriMatcher.match(uri)

        // Then: NO_MATCH 반환
        result shouldBe UriMatcher.NO_MATCH
    }

    @Test
    fun `숫자 와일드카드(#)를 사용한 패턴 매칭을 할 수 있다`() {
        // Given: 숫자 와일드카드를 포함한 패턴 등록
        val authority = "com.example.app.provider"
        val bookCode = 1
        val bookIdCode = 2

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        val bookListUri = Uri.parse("content://$authority/books/fiction")
        val specificBookUri = Uri.parse("content://$authority/books/fiction/123")

        // When: 숫자가 포함된 URI로 매칭
        uriMatcher.addURI(authority, "books", bookCode)          // /books
        uriMatcher.addURI(authority, "books/#", bookIdCode)      // /books/123

        // Then: 각각 다른 코드로 매칭됨
        uriMatcher.match(bookListUri) shouldBe bookCode
        uriMatcher.match(specificBookUri) shouldBe bookIdCode
    }

    @Test
    fun `문자열 와일드카드(*)를 사용한 패턴 매칭을 할 수 있다`() {
        // Given: 문자열 와일드카드를 포함한 패턴 등록
        val authority = "com.example.app.provider"
        val booksCode = 3

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        uriMatcher.addURI(authority, "books/*", booksCode)        // /books/fiction

        // When: 문자열이 포함된 URI로 매칭
        val fictionBooksUri = Uri.parse("content://$authority/books/fiction")
        val scienceBooksUri = Uri.parse("content://$authority/books/science")

        // Then: 둘 다 같은 코드로 매칭됨
        uriMatcher.match(fictionBooksUri) shouldBe booksCode
        uriMatcher.match(scienceBooksUri) shouldBe booksCode
    }

    @Test
    fun `복합적인 URI 패턴을 구분할 수 있다`() {
        // Given: 여러 패턴이 등록된 UriMatcher
        val authority = "com.example.app.provider"
        val booksCode = 1
        val bookIdCode = 2
        val authorsCode = 3
        val authorIdCode = 4

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        uriMatcher.addURI(authority, "books", booksCode)          // 전체 책 목록
        uriMatcher.addURI(authority, "books/#", bookIdCode)      // 특정 책
        uriMatcher.addURI(authority, "authors", authorsCode)      // 전체 작가 목록
        uriMatcher.addURI(authority, "authors/#", authorIdCode)  // 특정 작가

        // When: 다양한 URI로 매칭 테스트
        val uris = mapOf(
            "content://$authority/books" to booksCode,
            "content://$authority/books/123" to bookIdCode,
            "content://$authority/authors" to authorsCode,
            "content://$authority/authors/456" to authorIdCode,
            "content://$authority/unknown" to UriMatcher.NO_MATCH
        )

        // Then: 각각 올바른 코드로 매칭됨
        uris.forEach { (uriString, expectedCode) ->
            val uri = Uri.parse(uriString)
            uriMatcher.match(uri) shouldBe expectedCode
        }
    }

    @Test
    fun `실제 ContentProvider에서 사용하는 패턴을 학습한다`() {
        // Given: ContentProvider에서 사용할 법한 복잡한 패턴들
        val authority = "com.example.app.provider"

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        uriMatcher.addURI(authority, "books", 100)                    // 전체 책
        uriMatcher.addURI(authority, "books/#", 101)                  // 특정 책
        uriMatcher.addURI(authority, "books/#/reviews", 102)          // 특정 책의 리뷰들
        uriMatcher.addURI(authority, "books/#/reviews/#", 103)        // 특정 책의 특정 리뷰
        uriMatcher.addURI(authority, "authors/*/books", 104)          // 특정 작가의 책들

        // When & Then: 다양한 복잡한 URI 패턴 테스트
        uriMatcher.match(Uri.parse("content://$authority/books")) shouldBe 100
        uriMatcher.match(Uri.parse("content://$authority/books/42")) shouldBe 101
        uriMatcher.match(Uri.parse("content://$authority/books/42/reviews")) shouldBe 102
        uriMatcher.match(Uri.parse("content://$authority/books/42/reviews/7")) shouldBe 103
        uriMatcher.match(Uri.parse("content://$authority/authors/tolkien/books")) shouldBe 104
    }
}
