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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Serializable
private object ScreenA

@Serializable
private object ScreenB

@Serializable
private object ScreenC

@Composable
fun PopBackStackSample(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Column(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = ScreenA,
            modifier = Modifier.weight(1f),
        ) {
            composable<ScreenA> {
                ScreenContent(
                    screenName = "Screen A",
                    description = "첫 번째 화면입니다.\nScreen B로 이동할 수 있습니다.",
                    buttons =
                        listOf(
                            "Screen B로 이동" to { navController.navigate(ScreenB) },
                        ),
                )
            }
            composable<ScreenB> {
                ScreenContent(
                    screenName = "Screen B",
                    description = "두 번째 화면입니다.\nScreen C로 이동하거나 뒤로 갈 수 있습니다.",
                    buttons =
                        listOf(
                            "Screen C로 이동" to { navController.navigate(ScreenC) },
                            "popBackStack()" to { navController.popBackStack() },
                        ),
                )
            }
            composable<ScreenC> {
                ScreenContent(
                    screenName = "Screen C",
                    description = "세 번째 화면입니다.\npopBackStack()으로 이전 화면으로 돌아갑니다.",
                    buttons =
                        listOf(
                            "popBackStack()" to { navController.popBackStack() },
                        ),
                )
            }
        }
        BackStackDisplay(navController = navController)
    }
}

@Composable
private fun ScreenContent(
    screenName: String,
    description: String,
    buttons: List<Pair<String, () -> Unit>>,
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
            text = screenName,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        buttons.forEach { (label, onClick) ->
            Button(
                onClick = onClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
            ) {
                Text(text = label)
            }
        }
    }
}

@Composable
private fun BackStackDisplay(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val backStackRoutes by remember(backStackEntry) {
        derivedStateOf {
            navController.currentBackStack.value
                .mapNotNull { entry ->
                    entry.destination.route?.substringAfterLast(".")
                }
        }
    }

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "백스택 상태 (${backStackRoutes.size}개)",
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = backStackRoutes.joinToString(" → "),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PopBackStackSamplePreview() {
    MaterialTheme {
        PopBackStackSample()
    }
}
