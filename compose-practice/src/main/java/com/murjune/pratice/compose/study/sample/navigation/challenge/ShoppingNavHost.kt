package com.murjune.pratice.compose.study.sample.navigation.challenge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ShoppingNavHost(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO: 챌린지 구현
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Shopping Challenge - 구현해보세요!")
    }
}
