package com.murjune.pratice.compose.study.sample

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Counter(
    modifier: Modifier = Modifier,
    count: Int,
    onPlusCount: () -> Unit,
    onRefreshCount: () -> Unit,
) {
    Column(
        modifier.border(1.dp, color = Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row {
            Icon(
                modifier = Modifier.clickable { onPlusCount() },
                imageVector = Icons.Default.Add,
                contentDescription = "Plus Count",
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Icon(
                modifier = Modifier.testTag("Refresh").clickable { onRefreshCount() },
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh Count",
            )
        }
        Spacer(modifier = Modifier.padding(16.dp))
        Text(text = "$count")
        Spacer(modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SampleScreenPreview() {
    Counter(count = 0, onPlusCount = {}, onRefreshCount = {})
}
