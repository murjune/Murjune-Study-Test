package com.murjune.pratice.compose.study.sample.lazycomposable.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
private fun LazyColumnSample() {
    val messages by remember {
        mutableStateOf(Message.fakeList(100))
    }

    LazyColumn(
        modifier = Modifier
            .background(Color.Yellow)
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = messages,
        ) { message ->
            MessageRow(
                modifier = Modifier.fillMaxWidth(),
                message = message
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun LazyColumnWithItemSample() {
    val messages by remember {
        mutableStateOf(Message.fakeList(100))
    }

    LazyColumn(
        modifier = Modifier
            .background(Color.Yellow)
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(text = "Header", style = MaterialTheme.typography.headlineMedium)
        }

        itemsIndexed(
            items = messages,
        ) { index, message ->
            MessageRow(
                modifier = Modifier.fillMaxWidth(),
                message = message,
                index = index,
            )
        }

        item {
            Text(text = "Footer", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
private fun MessageRow(
    modifier: Modifier,
    message: Message,
    index: Int? = null,
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            val authorText = if (index != null) {
                "${message.author} - index: $index"
            } else {
                message.author
            }
            Text(
                text = authorText,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = message.body,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private data class Message(
    val id: Int,
    val author: String,
    val body: String,
) {
    companion object {
        fun fakeList(size: Int): List<Message> {
            return List(size) {
                Message(
                    id = it,
                    author = "Author $it",
                    body = "This is the body of message number $it. ".repeat((1..5).random())
                )
            }
        }
    }
}