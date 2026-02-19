package com.murjune.pratice.compose.study.sample.lazycomposable.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * GridCells.Fixed - 고정된 열 개수
 * - 3개의 열로 그리드를 구성
 * - 화면 크기에 상관없이 항상 3열 유지
 */
@Preview(showBackground = true)
@Composable
private fun LazyVerticalGridFixedSample() {
    val items by remember {
        mutableStateOf(GridItem.fakeList(30))
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = items,
            key = { it.id },
        ) { item ->
            GridItemCard(
                item = item,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
            )
        }
    }
}

/**
 * GridCells.Adaptive - 자동으로 열 개수 조정
 * - 최소 130dp 크기를 유지하면서 가능한 많은 열을 배치
 * - 화면 크기에 따라 열 개수가 자동으로 조정됨
 */
@Preview(showBackground = true)
@Composable
private fun LazyVerticalGridAdaptiveSample() {
    val items by remember {
        mutableStateOf(GridItem.fakeList(30))
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 130.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = items,
            key = { it.id },
        ) { item ->
            GridItemCard(
                item = item,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
            )
        }
    }
}

/**
 * StaggeredGridCells.Fixed - 고정된 열 개수의 엇갈린 그리드
 * - 2개의 열로 구성된 Pinterest 스타일 레이아웃
 * - 각 아이템이 서로 다른 높이를 가질 수 있음
 */
@Preview(showBackground = true)
@Composable
private fun LazyVerticalStaggeredGridFixedSample() {
    val items by remember {
        mutableStateOf(StaggeredItem.fakeList(30))
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
    ) {
        items(
            items = items,
            key = { it.id },
        ) { item ->
            StaggeredItemCard(
                item = item,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * StaggeredGridCells.Adaptive - 자동으로 열 개수 조정되는 엇갈린 그리드
 * - 최소 120dp 크기를 유지하면서 가능한 많은 열을 배치
 * - 화면 크기에 따라 열 개수가 자동으로 조정됨
 * - Pinterest나 Google Photos 스타일의 반응형 레이아웃
 */
@Preview(showBackground = true)
@Composable
private fun LazyVerticalStaggeredGridAdaptiveSample() {
    val items by remember {
        mutableStateOf(StaggeredItem.fakeList(30))
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = 120.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
    ) {
        items(
            items = items,
            key = { it.id },
        ) { item ->
            StaggeredItemCard(
                item = item,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun GridItemCard(
    item: GridItem,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(item.color),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun StaggeredItemCard(
    item: StaggeredItem,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(item.height.dp)
                    .background(item.color)
                    .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
        }
    }
}

private data class GridItem(
    val id: Int,
    val title: String,
    val color: Color,
) {
    companion object {
        fun fakeList(size: Int): List<GridItem> {
            val colors =
                listOf(
                    Color(0xFF6200EE),
                    Color(0xFF3700B3),
                    Color(0xFF03DAC5),
                    Color(0xFF018786),
                    Color(0xFFBB86FC),
                    Color(0xFF3F51B5),
                    Color(0xFF009688),
                    Color(0xFFFF5722),
                )

            return List(size) { index ->
                GridItem(
                    id = index,
                    title = "Item ${index + 1}",
                    color = colors[index % colors.size],
                )
            }
        }
    }
}

private data class StaggeredItem(
    val id: Int,
    val title: String,
    val color: Color,
    val height: Int,
) {
    companion object {
        fun fakeList(size: Int): List<StaggeredItem> {
            val colors =
                listOf(
                    Color(0xFF6200EE),
                    Color(0xFF3700B3),
                    Color(0xFF03DAC5),
                    Color(0xFF018786),
                    Color(0xFFBB86FC),
                    Color(0xFF3F51B5),
                    Color(0xFF009688),
                    Color(0xFFFF5722),
                )

            return List(size) { index ->
                StaggeredItem(
                    id = index,
                    title = "Item ${index + 1}",
                    color = colors[index % colors.size],
                    height = (100..300).random(),
                )
            }
        }
    }
}

/**
 * GridItemSpan 사용 예제 - 다양한 span 조합
 * - 헤더: 전체 너비(3열 모두 차지)
 * - Featured 아이템: 2열 차지
 * - 일반 아이템: 1열 차지
 * - 앱 스토어나 쇼핑몰 레이아웃에서 자주 사용되는 패턴
 */
@Preview(showBackground = true)
@Composable
private fun LazyVerticalGridWithSpanSample() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // 헤더 - 전체 너비 (maxLineSpan = 3열 모두 차지)
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(Color(0xFF6200EE))
                            .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Header - Span 3",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                    )
                }
            }
        }

        // Featured 아이템들 - 2열 차지
        items(
            count = 2,
            span = { GridItemSpan(2) },
        ) { index ->
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFF03DAC5)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Featured ${index + 1}\nSpan 2",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                    )
                }
            }
        }

        // 일반 아이템들 - 1열 차지 (기본값)
        items(count = 12) { index ->
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(Color(0xFF3F51B5)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                    )
                }
            }
        }

        // 중간 배너 - 전체 너비
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color(0xFFFF5722))
                            .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Banner - Span 3",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                    )
                }
            }
        }

        // 더 많은 일반 아이템들
        items(count = 9) { index ->
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(Color(0xFF009688)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${index + 13}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

/**
 * 조건부 Span 사용 예제
 * - 특정 인덱스의 아이템만 span을 다르게 설정
 * - 실제 데이터의 타입이나 조건에 따라 동적으로 span 조정
 */
@Preview(showBackground = true)
@Composable
private fun LazyVerticalGridWithConditionalSpanSample() {
    val items by remember {
        mutableStateOf(
            List(20) { index ->
                SpanItem(
                    id = index,
                    title = "Item ${index + 1}",
                    isFeatured = index % 7 == 0, // 7번째마다 Featured
                )
            },
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = items,
            key = { it.id },
            span = { item ->
                // 조건에 따라 span 결정
                if (item.isFeatured) {
                    GridItemSpan(2) // Featured 아이템은 2열 차지
                } else {
                    GridItemSpan(1) // 일반 아이템은 1열
                }
            },
        ) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(if (item.isFeatured) 2f else 1f)
                            .background(
                                if (item.isFeatured) Color(0xFFFF5722) else Color(0xFF3F51B5),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text =
                            if (item.isFeatured) {
                                "${item.title}\n★ Featured"
                            } else {
                                item.title
                            },
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

private data class SpanItem(
    val id: Int,
    val title: String,
    val isFeatured: Boolean,
)

/**
 * Adaptive + Span 조합 예제
 * - GridCells.Adaptive로 화면 크기에 따라 열 개수 자동 조정
 * - GridItemSpan으로 특정 아이템이 여러 열 차지
 * - 반응형 레이아웃에서도 span이 정상 작동
 *
 * 동작:
 * - 작은 화면(2열): Featured는 전체 너비, 일반은 반 너비
 * - 큰 화면(4열): Featured는 절반 너비, 일반은 1/4 너비
 */
@Preview(showBackground = true, widthDp = 360)
@Composable
private fun LazyVerticalGridAdaptiveWithSpanSample() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // 헤더 - 전체 너비
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(Color(0xFF6200EE))
                            .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Adaptive Header",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                    )
                }
            }
        }

        // Featured - 2열 차지 (화면 크기에 따라 상대적)
        items(
            count = 3,
            span = { GridItemSpan(2) },
        ) { index ->
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color(0xFFFF5722)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Featured ${index + 1}\n(Span 2)",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                    )
                }
            }
        }

        // 일반 아이템 - 1열 (기본값)
        items(count = 20) { index ->
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(Color(0xFF3F51B5)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                    )
                }
            }
        }
    }
}
