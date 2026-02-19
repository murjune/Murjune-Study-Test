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
import androidx.compose.material3.OutlinedButton
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
private object Login

@Serializable
private object Verify

@Serializable
private object PopUpToHome

@Composable
fun PopUpToSample(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Column(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Login,
            modifier = Modifier.weight(1f),
        ) {
            composable<Login> {
                LoginScreen(
                    onLogin = {
                        navController.navigate(Verify)
                    },
                )
            }
            composable<Verify> {
                VerifyScreen(
                    onVerified = {
                        navController.navigate(PopUpToHome) {
                            popUpTo<Login> { inclusive = true }
                        }
                    },
                )
            }
            composable<PopUpToHome> {
                PopUpToHomeScreen(
                    onLaunchSingleTopTest = {
                        navController.navigate(PopUpToHome) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateWithoutSingleTop = {
                        navController.navigate(PopUpToHome)
                    },
                )
            }
        }
        PopUpToBackStackDisplay(navController = navController)
    }
}

@Composable
private fun LoginScreen(
    onLogin: () -> Unit,
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
            text = "Login 화면",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "로그인 후 Verify → Home으로 이동합니다.\npopUpTo(Login) { inclusive = true }로\nLogin이 백스택에서 제거됩니다.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogin) {
            Text(text = "로그인")
        }
    }
}

@Composable
private fun VerifyScreen(
    onVerified: () -> Unit,
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
            text = "Verify 화면",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "인증 완료 시 Home으로 이동하며,\nLogin 화면이 백스택에서 제거됩니다.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onVerified) {
            Text(text = "인증 완료")
        }
    }
}

@Composable
private fun PopUpToHomeScreen(
    onLaunchSingleTopTest: () -> Unit,
    onNavigateWithoutSingleTop: () -> Unit,
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
            text = "Home 화면",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Login이 백스택에서 제거되었습니다.\n아래 버튼으로 launchSingleTop 차이를 확인하세요.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onLaunchSingleTopTest,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "launchSingleTop = true (중복 방지)")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onNavigateWithoutSingleTop,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "launchSingleTop 없이 이동 (중복 생성)")
        }
    }
}

@Composable
private fun PopUpToBackStackDisplay(
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
private fun PopUpToSamplePreview() {
    MaterialTheme {
        PopUpToSample()
    }
}
