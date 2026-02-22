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
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
private object SingleTopHome

@Serializable
private data class SingleTopDetail(
    val id: Int,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchSingleTopSample(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "LaunchSingleTop") },
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
                startDestination = SingleTopHome,
                modifier = Modifier.weight(1f),
            ) {
                composable<SingleTopHome> {
                    HomeScreen(navController = navController)
                }
                composable<SingleTopDetail> { backStackEntry ->
                    val route = backStackEntry.toRoute<SingleTopDetail>()
                    DetailScreen(
                        id = route.id,
                        navController = navController,
                    )
                }
            }
            BackStackDisplay(navController = navController)
        }
    }
}

@Composable
private fun HomeScreen(
    navController: NavHostController,
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
                    text = "launchSingleTop 설명",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text =
                        """
                        • launchSingleTop = true로 navigate하면 백스택의 top이 같은 destination일 때 새 엔트리를 추가하지 않습니다
                        • 대신 기존 엔트리의 인자만 새 인자로 갱신됩니다
                        • top이 다른 destination이면 launchSingleTop이어도 정상적으로 쌓입니다
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
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate(SingleTopDetail(id = 1)) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
        ) {
            Text(text = "Detail(id=1)로 이동")
        }
    }
}

@Composable
private fun DetailScreen(
    id: Int,
    navController: NavHostController,
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
            text = "Detail 화면 (id=$id)",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "현재 Detail의 id는 $id 입니다",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                navController.navigate(SingleTopDetail(id = id + 1)) {
                    launchSingleTop = true
                }
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
        ) {
            Text(text = "launchSingleTop으로 Detail(id=${id + 1})로 이동")
        }
        OutlinedButton(
            onClick = {
                navController.navigate(SingleTopDetail(id = id + 1))
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
        ) {
            Text(text = "일반 navigate로 Detail(id=${id + 1})로 이동")
        }
        OutlinedButton(
            onClick = { navController.popBackStack() },
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
                    val route = entry.destination.route?.substringAfterLast(".") ?: return@mapNotNull null
                    if (route.startsWith("SingleTopDetail")) {
                        val args = entry.arguments
                        val id = args?.getInt("id") ?: 0
                        "SingleTopDetail($id)"
                    } else {
                        route
                    }
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
private fun LaunchSingleTopSamplePreview() {
    MaterialTheme {
        LaunchSingleTopSample()
    }
}
