package com.murjune.pratice.compose.study

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(AndroidJUnit4::class)
class UriTest : BehaviorSpec({

    Given("웹사이트 URL을 파싱할 때") {
        val urlString = "https://example.com/path?param=value&foo=bar"

        When("Uri.parse()를 사용하면") {
            val uri = Uri.parse(urlString)
            
            Then("각 구성 요소를 안전하게 추출할 수 있다") {
                uri.scheme shouldBe "https"
                uri.host shouldBe "example.com"
                uri.path shouldBe "/path"
                uri.getQueryParameter("param") shouldBe "value"
                uri.getQueryParameter("foo") shouldBe "bar"
            }
        }
    }

    Given("딥링크 URL을 다룰 때") {
        val deepLinkUrl = "myapp://user/123?name=john&age=30"
        
        When("Uri.parse()로 파싱하면") {
            val uri = Uri.parse(deepLinkUrl)
            
            Then("커스텀 스키마와 파라미터를 추출할 수 있다") {
                uri.scheme shouldBe "myapp"
                uri.host shouldBe "user"
                uri.path shouldBe "/123"
                uri.getQueryParameter("name") shouldBe "john"
                uri.getQueryParameter("age") shouldBe "30"
            }
        }
    }

    Given("파일 경로를 다룰 때") {
        val filePath = "/storage/emulated/0/Documents/test.txt"
        
        When("Uri.fromFile()을 사용하면") {
            val fileUri = Uri.fromFile(java.io.File(filePath))
            
            Then("파일 URI가 올바르게 생성된다") {
                fileUri.scheme shouldBe "file"
                fileUri.path shouldBe filePath
                fileUri.toString() shouldStartWith "file://"
            }
        }
    }

    Given("Content URI를 다룰 때") {
        val contentUri = "content://com.example.provider/items/123"
        
        When("Uri.parse()로 파싱하면") {
            val uri = Uri.parse(contentUri)
            
            Then("Content Provider 정보를 추출할 수 있다") {
                uri.scheme shouldBe "content"
                uri.authority shouldBe "com.example.provider"
                uri.path shouldBe "/items/123"
                uri.lastPathSegment shouldBe "123"
            }
        }
    }

    Given("URI Builder를 사용할 때") {
        When("복잡한 URI를 단계별로 구성하면") {
            val uri = Uri.Builder()
                .scheme("https")
                .authority("api.example.com")
                .path("/v1/users")
                .appendQueryParameter("page", "1")
                .appendQueryParameter("size", "10")
                .appendQueryParameter("sort", "name,asc")
                .build()
            
            Then("올바른 URI가 생성된다") {
                uri.toString() shouldBe "https://api.example.com/v1/users?page=1&size=10&sort=name%2Casc"
                uri.getQueryParameter("page") shouldBe "1"
                uri.getQueryParameter("size") shouldBe "10"
                uri.getQueryParameter("sort") shouldBe "name,asc"
            }
        }
    }

    Given("잘못된 URI 문자열이 주어질 때") {
        val malformedUrl = "not-a-valid-uri"
        
        When("Uri.parse()를 사용하면") {
            val uri = Uri.parse(malformedUrl)
            
            Then("파싱은 성공하지만 구성 요소들이 null이다") {
                uri shouldNotBe null
                uri.scheme shouldBe null
                uri.host shouldBe null
                uri.path shouldBe malformedUrl
            }
        }
    }

    Given("인코딩이 필요한 문자열을 다룰 때") {
        val queryWithSpecialChars = "Hello World & More"
        
        When("Uri.Builder로 안전하게 추가하면") {
            val uri = Uri.Builder()
                .scheme("https")
                .authority("example.com")
                .appendQueryParameter("message", queryWithSpecialChars)
                .build()
            
            Then("자동으로 URL 인코딩이 적용된다") {
                uri.toString() shouldContain "message=Hello%20World%20%26%20More"
                uri.getQueryParameter("message") shouldBe queryWithSpecialChars
            }
        }
    }

    Given("하드코딩된 문자열 조작과 비교할 때") {
        val baseUrl = "https://api.example.com"
        val path = "/users"
        val userId = "123"
        val param = "active"
        val value = "true"
        
        When("문자열 연결로 URL을 만들면") {
            val hardcodedUrl = "$baseUrl$path/$userId?$param=$value"
            
            Then("실수하기 쉽고 유지보수가 어렵다") {
                hardcodedUrl shouldBe "https://api.example.com/users/123?active=true"
                
                // 하드코딩의 문제점을 보여주는 예시
                val problematicUrl = "$baseUrl$path/$userId?$param=$value&another=value with spaces"
                problematicUrl shouldContain "value with spaces" // 인코딩되지 않음
            }
        }
        
        When("Uri.Builder를 사용하면") {
            val safeUri = Uri.Builder()
                .scheme("https")
                .authority("api.example.com")
                .path(path)
                .appendPath(userId)
                .appendQueryParameter(param, value)
                .appendQueryParameter("another", "value with spaces")
                .build()
            
            Then("안전하고 유지보수가 쉬운 코드가 된다") {
                safeUri.toString() shouldBe "https://api.example.com/users/123?active=true&another=value%20with%20spaces"
                safeUri.getQueryParameter("another") shouldBe "value with spaces"
            }
        }
    }
})