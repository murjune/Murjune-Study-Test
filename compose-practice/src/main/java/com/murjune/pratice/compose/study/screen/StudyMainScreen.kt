package com.murjune.pratice.compose.study.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private data class StudyTopic(
    val id: String,
    val title: String,
    val description: String,
)

private val studyTopics: List<StudyTopic> =
    listOf(
        StudyTopic(
            id = "navigation",
            title = "Navigation",
            description = "Jetpack Compose Navigation 학습",
        ),
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyMainScreen(
    onTopicClick: (topicId: String, topicTitle: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Compose Study") },
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
                items = studyTopics,
                key = { it.id },
            ) { topic ->
                StudyTopicItem(
                    topic = topic,
                    onClick = { onTopicClick(topic.id, topic.title) },
                )
            }
        }
    }
}

@Composable
private fun StudyTopicItem(
    topic: StudyTopic,
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
                text = topic.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = topic.description,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StudyMainScreenPreview() {
    MaterialTheme {
        StudyMainScreen(
            onTopicClick = { _, _ -> },
        )
    }
}
