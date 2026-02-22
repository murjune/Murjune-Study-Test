package com.murjune.pratice.compose.study.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private data class SampleItem(
    val title: String,
    val route: Any,
)

private fun getSamplesForTopic(topicId: String): List<SampleItem> =
    when (topicId) {
        "navigation" ->
            listOf(
                SampleItem(
                    title = "NavHost 기본",
                    route = com.murjune.pratice.compose.study.navigation.NavHostBasicRoute,
                ),
                SampleItem(
                    title = "Type-Safe Navigation",
                    route = com.murjune.pratice.compose.study.navigation.TypeSafeNavRoute,
                ),
                SampleItem(
                    title = "PopBackStack",
                    route = com.murjune.pratice.compose.study.navigation.PopBackStackRoute,
                ),
                SampleItem(
                    title = "PopUpTo",
                    route = com.murjune.pratice.compose.study.navigation.PopUpToRoute,
                ),
                SampleItem(
                    title = "DeepLink",
                    route = com.murjune.pratice.compose.study.navigation.DeepLinkRoute,
                ),
                SampleItem(
                    title = "LaunchSingleTop",
                    route = com.murjune.pratice.compose.study.navigation.LaunchSingleTopRoute,
                ),
                SampleItem(
                    title = "SavedStateHandle",
                    route = com.murjune.pratice.compose.study.navigation.SavedStateHandleRoute,
                ),
                SampleItem(
                    title = "SaveRestore State",
                    route = com.murjune.pratice.compose.study.navigation.SaveRestoreStateRoute,
                ),
                SampleItem(
                    title = "Nested NavGraph",
                    route = com.murjune.pratice.compose.study.navigation.NestedNavGraphRoute,
                ),
                SampleItem(
                    title = "NavigateUp",
                    route = com.murjune.pratice.compose.study.navigation.NavigateUpRoute,
                ),
            )
        else -> emptyList()
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicScreen(
    topicId: String,
    topicTitle: String,
    onSampleClick: (route: Any) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val samples = getSamplesForTopic(topicId)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = topicTitle) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            items(
                items = samples,
                key = { it.title },
            ) { sample ->
                SampleListItem(
                    sample = sample,
                    onClick = { onSampleClick(sample.route) },
                )
            }
        }
    }
}

@Composable
private fun SampleListItem(
    sample: SampleItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = sample.title,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TopicScreenPreview() {
    MaterialTheme {
        TopicScreen(
            topicId = "navigation",
            topicTitle = "Navigation",
            onSampleClick = {},
            onBackClick = {},
        )
    }
}
