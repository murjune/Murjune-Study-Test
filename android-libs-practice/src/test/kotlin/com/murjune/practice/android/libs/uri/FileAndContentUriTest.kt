package com.murjune.practice.android.libs.uri

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FileAndContentUriTest {

    /**
     *   File URI
     *
     *   - 형태: file:// 스키마 사용
     *   - 접근 방식: 파일 시스템 직접 접근
     *   - 보안: 파일 경로 노출, 보안상 취약
     *   - 예시: file:///storage/emulated/0/Pictures/image.jpg
     *   - 용도: 앱 내부 파일, 캐시 파일 등
     *
     *   Android 7.0+ 에서 File URI 공유 시 FileUriExposedException 발생
     * */
    @Test
    fun `파일 경로를 다룰 때 Uri_fromFile()을 활용하면 파일 URI가 올바르게 생성된다`() {
        val filePath = "/storage/emulated/0/Documents/test.txt"
        val fileUri: Uri = Uri.fromFile(File(filePath))

        fileUri.scheme shouldBe "file"
        fileUri.path shouldBe filePath
        fileUri.toString() shouldStartWith "file://"
        fileUri.toString() shouldBe "file:///storage/emulated/0/Documents/test.txt"
    }

    /**
     *   Content URI
     *
     *   - 형태: content:// 스키마 사용
     *   - 접근 방식: ContentProvider를 통한 간접 접근
     *   - 보안: 권한 기반 접근 제어, 더 안전
     *   - 예시: content://media/external/images/media/123
     *   - 용도: 미디어 파일, 연락처, 캘린더 등 시스템 데이터
     *
     *   1. 보안: Content URI가 더 안전 (경로 노출 안됨)
     *   2. 권한: Content URI는 세밀한 권한 제어 가능
     *   3. 접근성: Content URI는 다른 앱 간 안전한 데이터 공유 가능
     * */
    @Test
    fun `Content URI를 다룰 때, Uri_parse()로 파싱하면, Content Provider 정보를 추출할 수 있다`() {
        val contentUri = "content://com.example.provider/items/123"
        val uri = Uri.parse(contentUri)

        uri.scheme shouldBe "content"
        uri.authority shouldBe "com.example.provider"
        uri.path shouldBe "/items/123"
        uri.lastPathSegment shouldBe "123"
    }
}