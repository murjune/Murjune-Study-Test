package com.murjune.pratice.compose.study.sample.lazycomposable.performance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Precision
import com.murjune.pratice.compose.study.R
import com.murjune.pratice.compose.study.sample.fake.Photo
import com.murjune.pratice.compose.study.sample.fake.rememberRandomSamplePhotos

/**
 * 로드할 Image 의 크기를 미리 가져오고
 * 핵심: 이미지가 로딩되기 전에 자리를 미리 잡아둡니다.
 *
 * 하지만, LazyColumn은 초기 측정 시 무한한 높이 제약 조건을 사용하므로 0px 크기 항목을 사용하면 모든 항목이 컴포지션되어 성능 문제를 일으킬 수 있으니 항상 placeholder로 초기 크기를 지정해야 한다.
 * https://developer.android.com/develop/ui/compose/lists#avoid-0-size-items
 * */
@Preview(showBackground = true)
@Composable
private fun ImprovementImageLoadWithAspectRatio() {
    val photos: List<Photo> = rememberRandomSamplePhotos(100)

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(130.dp),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier =
            Modifier
                .fillMaxSize()
                .systemBarsPadding(),
    ) {
        items(photos) { photo ->
            val aspectRatio = photo.width.toFloat() / photo.height.toFloat()
            AsyncImage(
                model = photo.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        // ★ 핵심: 이미지가 로딩되기 전에 자리를 미리 잡아둡니다.
                        .then(
                            if (photo.height == 0 || photo.width == 0) {
                                Modifier
                            } else {
                                Modifier.aspectRatio(aspectRatio)
                            },
                        ),
                placeholder = painterResource(R.drawable.ic_placeholder),
            )
        }
    }
}

/**
 * 로드할 Image 의 크기를 미리 가져오고
 * 핵심 1: 이미지가 로딩되기 전에 자리를 미리 잡아둡니다.
 * 핵심 2: 이미지 라이브러리(Coil) 최적화
 * */
@Preview(showBackground = true)
@Composable
private fun ImprovementImageLoadWitImageLoader() {
    val photos: List<Photo> = rememberRandomSamplePhotos(100)

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(130.dp),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier =
            Modifier
                .fillMaxSize()
                .systemBarsPadding(),
    ) {
        items(photos) { photo ->
            val aspectRatio = photo.width.toFloat() / photo.height.toFloat()
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(photo.url)
                        .crossfade(durationMillis = 300) // 부드러운 전환
                        // ★ 핵심: 가로세로 둘 다 200px '박스' 안에 들어오도록 요청하되,
                        // INEXACT(부정확함)를 줘서 비율을 깨지 않고 알아서 맞추게 함.
                        .size(200)
                        .precision(Precision.INEXACT)
                        .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        // ★ 핵심: 이미지가 로딩되기 전에 자리를 미리 잡아둡니다.
                        .then(
                            if (photo.height == 0 || photo.width == 0) {
                                Modifier
                            } else {
                                Modifier.aspectRatio(aspectRatio)
                            },
                        ),
                placeholder = painterResource(R.drawable.ic_placeholder),
            )
        }
    }
}
