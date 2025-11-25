package com.murjune.pratice.compose.study.sample.lazycomposable.basic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.murjune.pratice.compose.study.sample.fake.rememberRandomSampleImageUrls

/**
 * LazyVerticalStaggeredGrid + Adaptive 예제
 * - 최소 200dp 크기 유지하며 자동으로 열 개수 조정
 * - 다양한 크기의 이미지를 폭포수(Waterfall) 레이아웃으로 배치
 * - Google Photos, Pinterest 스타일
 * - AsyncImage로 네트워크 이미지 로딩 (Coil 사용)
 */
@Preview(showBackground = true)
@Composable
private fun LazyStaggeredGridAdaptiveImageSample() {
    val imageUrls = rememberRandomSampleImageUrls(100)
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(130.dp),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
        ,
    ) {
        items(imageUrls) { photo ->
            AsyncImage(
                model = photo,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            )
        }
    }
}

/**
 * LazyVerticalStaggeredGrid + Fixed 예제
 * - 고정된 3열 레이아웃
 * - 다양한 비율의 이미지가 자연스럽게 배치됨
 * - 세로/가로/정사각형 이미지 혼합
 * - Instagram 탐색 탭 스타일
 */
@Preview(showBackground = true)
@Composable
private fun LazyStaggeredGridFixedImageSample() {
    val imageUrls = rememberRandomSampleImageUrls(100)
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(3),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(imageUrls) { photo ->
            AsyncImage(
                model = photo,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                ,
            )
        }
    }
}

/**
 * 2열 고정 레이아웃 예제
 * - 좀 더 큰 이미지 표시
 * - 갤러리 앱 스타일
 */
@Preview(showBackground = true)
@Composable
private fun LazyStaggeredGridTwoColumnsImageSample() {
    val imageUrls = rememberRandomSampleImageUrls(100)

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
        ,
    ) {
        items(imageUrls) { photo ->
            AsyncImage(
                model = photo,
                contentScale = ContentScale.Crop,
                contentDescription = "Random image",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            )
        }
    }
}
