package com.murjune.pratice.compose.ui.practice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NormalScrollProgressSample()
        }
    }
}

@Composable
fun NormalScrollProgressSample() {
    val scrollState = rememberScrollState()

    // 0~1 스크롤 progress
    val progress = (scrollState.value.toFloat() / scrollState.maxValue)
        .coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        repeat(100) {
            Text("Item $it")
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Hello, World!",
            modifier = Modifier
                .graphicsLayer {
                    val minSize = 12f
                    val maxSize = 24f
                    val animated = lerp(minSize, maxSize, progress)
                    val scale = animated / minSize
                    scaleX = scale
                    scaleY = scale
                }
        )
    }
}

@Composable
fun ScrollScalingText() {
    val lazyListState = rememberLazyListState()
    val scrollOffset = minOf(
        200f, // 최대 스크롤 기준값 (조절 가능)
        (lazyListState.firstVisibleItemScrollOffset +
            lazyListState.firstVisibleItemIndex * 100).toFloat()
    )
    lazyListState.isScrollInProgress

    // 12dp ~ 24dp에 해당하는 scale 변환

    LazyColumn(state = lazyListState) {
        val minSize = 12f
        val maxSize = 24f
        val progress = scrollOffset / 200f  // 0 ~ 1
        item {
            Text(
                text = "Hello, World!",
                modifier = Modifier
                    .graphicsLayer {
                        val animatedSize = lerp(minSize, maxSize, progress)
                        val scale = animatedSize / minSize  // 12dp 기준 scale 계산
                        scaleX = scale
                        scaleY = scale
                    }
            )
        }

        items(100) {
            Text("Item $it")
        }
    }
}