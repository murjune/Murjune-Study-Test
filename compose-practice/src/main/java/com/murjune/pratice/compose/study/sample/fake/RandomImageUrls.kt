package com.murjune.pratice.compose.study.sample.fake

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

private val rangeForRandom = (0..100000)

/**
 * 랜덤 이미지 URL 생성
 * - picsum.photos API 사용
 * - seed: 같은 seed면 같은 이미지 반환
 * - width, height: 이미지 크기
 */
fun randomSampleImageUrl(
    seed: Int = rangeForRandom.random(),
    width: Int = 300,
    height: Int = width,
): String {
    return "https://picsum.photos/seed/$seed/$width/$height"
}

fun randomSamplePhoto(
    seed: Int = rangeForRandom.random(),
    width: Int = 300,
    height: Int = width,
): Photo {
    val url = randomSampleImageUrl(seed, width, height)
    return Photo(url = url, width = width, height = height)
}

/**
 * Composable에서 remember와 함께 사용하는 랜덤 이미지 URL
 * - 리컴포지션 시에도 같은 URL 유지
 */
@Composable
fun rememberRandomSampleImageUrl(
    seed: Int = rangeForRandom.random(),
    width: Int = 300,
    height: Int = width,
): String = remember { randomSampleImageUrl(seed, width, height) }


@Composable
fun rememberRandomSamplePhoto(
    seed: Int = rangeForRandom.random(),
    width: Int = 300,
    height: Int = width,
): Photo = remember {
    randomSamplePhoto(seed, width, height)
}

@Composable
fun rememberRandomSampleImageUrls(
    count: Int,
    width: Int,
    height: Int,
): List<String> = remember {
    List(count) {
        randomSampleImageUrl(
            seed = rangeForRandom.random(),
            width = width,
            height = height,
        )
    }
}

@Composable
fun rememberRandomSamplePhotos(
    count: Int,
): List<Photo> = remember {
    List(count) {
        randomSamplePhotos.random()
    }
}

@Composable
fun rememberRandomSampleImageUrls(
    count: Int
): List<String> = remember(count) {
    List(count) {
        randomSizedImageUrls.random()
    }
}

/**
 * 다양한 크기의 랜덤 이미지 리스트
 * - 세로 긴 이미지 (900x1600)
 * - 가로 긴 이미지 (1600x900)
 * - 정사각형 (500x500)
 * - 작은 이미지 (300x400)
 */
private val randomSizedImageUrls = listOf(
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 900, height = 1600),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 300, height = 400),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 900, height = 1600),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 300, height = 400),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 900, height = 1600),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 300, height = 400),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 300, height = 400),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 900, height = 1600),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 300, height = 400),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 500, height = 500),
)

private val randomSamplePhotos: List<Photo> = listOf(
    Photo(
        url = randomSampleImageUrl(width = 1600, height = 900),
        width = 1600,
        height = 900,
    ),
    Photo(
        url = randomSampleImageUrl(width = 900, height = 1600),
        width = 900,
        height = 1600,
    ),
    Photo(
        url = randomSampleImageUrl(width = 500, height = 500),
        width = 500,
        height = 500,
    ),
    Photo(
        url = randomSampleImageUrl(width = 300, height = 400),
        width = 300,
        height = 400,
    ),

    Photo(
        url = randomSampleImageUrl(width = 1600, height = 900),
        width = 1600,
        height = 900,
    ),
    Photo(
        url = randomSampleImageUrl(width = 500, height = 500),
        width = 500,
        height = 500,
    ),

    Photo(
        url = randomSampleImageUrl(width = 1600, height = 900),
        width = 1600,
        height = 900,
    ),
    Photo(
        url = randomSampleImageUrl(width = 900, height = 1600),
        width = 900,
        height = 1600,
    ),
    Photo(
        url = randomSampleImageUrl(width = 500, height = 500),
        width = 500,
        height = 500,
    ),
    Photo(
        url = randomSampleImageUrl(width = 300, height = 400),
        width = 300,
        height = 400,
    ),

    Photo(
        url = randomSampleImageUrl(width = 1600, height = 900),
        width = 1600,
        height = 900,
    ),
    Photo(
        url = randomSampleImageUrl(width = 500, height = 500),
        width = 500,
        height = 500,
    ),

    Photo(
        url = randomSampleImageUrl(width = 900, height = 1600),
        width = 900,
        height = 1600,
    ),
    Photo(
        url = randomSampleImageUrl(width = 500, height = 500),
        width = 500,
        height = 500,
    ),
    Photo(
        url = randomSampleImageUrl(width = 300, height = 400),
        width = 300,
        height = 400,
    ),

    Photo(
        url = randomSampleImageUrl(width = 1600, height = 900),
        width = 1600,
        height = 900,
    ),
    Photo(
        url = randomSampleImageUrl(width = 500, height = 500),
        width = 500,
        height = 500,
    ),

    Photo(
        url = randomSampleImageUrl(width = 500, height = 500),
        width = 500,
        height = 500,
    ),
    Photo(
        url = randomSampleImageUrl(width = 300, height = 400),
        width = 300,
        height = 400,
    ),

    Photo(
        url = randomSampleImageUrl(width = 1600, height = 900),
        width = 1600,
        height = 900,
    ),
    Photo(
        url = randomSampleImageUrl(width = 500, height = 500),
        width = 500,
        height = 500,
    ),
    Photo(
        url = randomSampleImageUrl(width = 900, height = 1600),
        width = 900,
        height = 1600,
    ),
    Photo(
        url = randomSampleImageUrl(width = 500, height = 500),
        width = 500,
        height = 500,
    ),
    Photo(
        url = randomSampleImageUrl(width = 300, height = 400),
        width = 300,
        height = 400,
    ),

    Photo(
        url = randomSampleImageUrl(width = 1600, height = 900),
        width = 1600,
        height = 900,
    ),
    Photo(
        url = randomSampleImageUrl(width = 500, height = 500),
        width = 500,
        height = 500,
    ),
)

data class Photo(val url: String, val width: Int, val height: Int)