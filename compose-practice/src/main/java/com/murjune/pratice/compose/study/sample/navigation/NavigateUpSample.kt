package com.murjune.pratice.compose.study.sample.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
private object NavigateUpHome

@Serializable
private object NavUpScreenA

@Serializable
private object NavUpScreenB

@Serializable
private object NavUpScreenC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigateUpSample(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    var resultMessage by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "NavigateUp vs PopBackStack") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = NavigateUpHome,
                modifier = Modifier.weight(1f),
            ) {
                composable<NavigateUpHome> {
                    HomeScreenContent(
                        navController = navController,
                        resultMessage = resultMessage,
                    )
                }
                composable<NavUpScreenA> {
                    ScreenContentWithNavigation(
                        screenName = "Screen A",
                        description = "첫 번째 화면입니다.\nScreen B로 이동하거나 뒤로 갈 수 있습니다.",
                        nextScreen = { navController.navigate(NavUpScreenB) },
                        navController = navController,
                        onResult = { resultMessage = it },
                    )
                }
                composable<NavUpScreenB> {
                    ScreenContentWithNavigation(
                        screenName = "Screen B",
                        description = "두 번째 화면입니다.\nScreen C로 이동하거나 뒤로 갈 수 있습니다.",
                        nextScreen = { navController.navigate(NavUpScreenC) },
                        navController = navController,
                        onResult = { resultMessage = it },
                    )
                }
                composable<NavUpScreenC> {
                    ScreenContentWithNavigation(
                        screenName = "Screen C",
                        description = "세 번째 화면입니다.\n뒤로 갈 수 있습니다.",
                        nextScreen = null,
                        navController = navController,
                        onResult = { resultMessage = it },
                    )
                }
            }
            BackStackDisplay(navController = navController)
        }
    }
}

@Composable
private fun HomeScreenContent(
    navController: NavHostController,
    resultMessage: String,
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
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "navigateUp vs popBackStack 차이",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text =
                        """
                        • navigateUp(): 앱 계층 구조의 "위"로 이동 (semantic up navigation)
                          - parent NavController가 있으면 위임합니다
                          - Deep link나 중첩된 NavGraph에서 의미 있는 동작을 합니다

                        • popBackStack(): 백스택에서 top 엔트리 제거 (stack operation)
                          - 단순히 스택 최상단 항목을 제거합니다

                        • 단순 NavHost에서는 두 메서드가 동일하게 동작합니다
                        • Deep link나 parent NavController가 있을 때 차이가 발생합니다
                        """.trimIndent(),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Home 화면",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (resultMessage.isNotEmpty()) {
            Text(
                text = "결과: $resultMessage",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate(NavUpScreenA) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
        ) {
            Text(text = "Screen A로 이동")
        }
    }
}

@Composable
private fun ScreenContentWithNavigation(
    screenName: String,
    description: String,
    nextScreen: (() -> Unit)?,
    navController: NavHostController,
    onResult: (String) -> Unit,
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
        if (nextScreen != null) {
            Button(
                onClick = nextScreen,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
            ) {
                Text(text = "다음 화면으로 이동")
            }
        }
        Button(
            onClick = {
                val result = navController.navigateUp()
                onResult("navigateUp() → $result")
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
        ) {
            Text(text = "navigateUp()")
        }
        OutlinedButton(
            onClick = {
                val result = navController.popBackStack()
                onResult("popBackStack() → $result")
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
        ) {
            Text(text = "popBackStack()")
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
private fun NavigateUpSamplePreview() {
    MaterialTheme {
        NavigateUpSample()
    }
}
