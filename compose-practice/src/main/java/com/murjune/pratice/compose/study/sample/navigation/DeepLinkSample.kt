package com.murjune.pratice.compose.study.sample.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

private const val DEEP_LINK_BASE_PATH = "https://study.example.com/profile"

@Serializable
private object DeepLinkHome

@Serializable
private data class DeepLinkProfile(
    val userId: String,
)

@Composable
fun DeepLinkSample(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = DeepLinkHome,
        modifier = modifier.fillMaxSize(),
    ) {
        composable<DeepLinkHome> {
            DeepLinkHomeScreen(
                onNavigateToProfile = {
                    navController.navigate(DeepLinkProfile(userId = "user_123"))
                },
            )
        }
        composable<DeepLinkProfile>(
            deepLinks =
                listOf(
                    navDeepLink<DeepLinkProfile>(basePath = DEEP_LINK_BASE_PATH),
                ),
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<DeepLinkProfile>()
            DeepLinkProfileScreen(
                userId = route.userId,
                onBack = {
                    navController.popBackStack()
                },
            )
        }
    }
}

@Composable
private fun DeepLinkHomeScreen(
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "DeepLink Sample",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "딥링크 URI",
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$DEEP_LINK_BASE_PATH/{userId}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "adb 테스트 명령어",
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text =
                        "adb shell am start -a android.intent.action.VIEW " +
                            "-d \"$DEEP_LINK_BASE_PATH/user_42\"",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToProfile) {
            Text(text = "프로필 화면으로 이동 (userId = user_123)")
        }
    }
}

@Composable
private fun DeepLinkProfileScreen(
    userId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "프로필 화면",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "userId = $userId",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "딥링크 또는 일반 navigate()로 진입 가능합니다.\n전달된 인자가 위에 표시됩니다.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text(text = "뒤로가기")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeepLinkSamplePreview() {
    MaterialTheme {
        DeepLinkSample()
    }
}
