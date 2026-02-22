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
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
private object NestHome

@Serializable
private object AuthGraph

@Serializable
private object NestedLogin

@Serializable
private object Signup

@Serializable
private object VerifyEmail

@Serializable
private object Dashboard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NestedNavGraphSample(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "중첩 네비게이션 그래프") },
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
                startDestination = NestHome,
                modifier = Modifier.weight(1f),
            ) {
                composable<NestHome> {
                    HomeScreen(
                        onNavigateToAuth = { navController.navigate(AuthGraph) },
                    )
                }

                navigation<AuthGraph>(startDestination = NestedLogin) {
                    composable<NestedLogin> {
                        LoginScreen(
                            onLoginClick = { navController.navigate(VerifyEmail) },
                            onSignupClick = { navController.navigate(Signup) },
                        )
                    }

                    composable<Signup> {
                        SignupScreen(
                            onNextClick = { navController.navigate(VerifyEmail) },
                            onBackClick = { navController.popBackStack() },
                        )
                    }

                    composable<VerifyEmail> {
                        VerifyEmailScreen(
                            onVerifyComplete = {
                                navController.navigate(Dashboard) {
                                    popUpTo<AuthGraph> { inclusive = true }
                                }
                            },
                        )
                    }
                }

                composable<Dashboard> {
                    DashboardScreen()
                }
            }
            BackStackDisplay(navController = navController)
        }
    }
}

@Composable
private fun HomeScreen(
    onNavigateToAuth: () -> Unit,
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
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "중첩 네비게이션 그래프",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text =
                        """
                        • navigation<GraphRoute>(startDestination) {}로 중첩 그래프 생성
                        • 그래프로 navigate 시 startDestination 자동 표시
                        • popUpTo<GraphRoute> { inclusive = true }로 그래프 전체 한번에 정리
                        • 중첩 그래프는 독립적 서브 플로우를 캡슐화
                        """.trimIndent(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        Text(
            text = "홈 화면",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "인증 플로우를 중첩 그래프로 체험해보세요",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNavigateToAuth,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "로그인하기")
        }
    }
}

@Composable
private fun LoginScreen(
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
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
            text = "로그인",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Auth 중첩 그래프의 startDestination",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        ) {
            Text(text = "로그인")
        }
        Button(
            onClick = onSignupClick,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        ) {
            Text(text = "회원가입")
        }
    }
}

@Composable
private fun SignupScreen(
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
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
            text = "회원가입",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Auth 중첩 그래프 내부 화면",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNextClick,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        ) {
            Text(text = "다음")
        }
        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        ) {
            Text(text = "뒤로")
        }
    }
}

@Composable
private fun VerifyEmailScreen(
    onVerifyComplete: () -> Unit,
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
            text = "이메일 인증",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "인증 완료 후 Dashboard로 이동하며\nAuth 그래프 전체가 백스택에서 제거됩니다",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onVerifyComplete,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "인증 완료")
        }
    }
}

@Composable
private fun DashboardScreen(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "대시보드",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "로그인 완료!\npopBackStack 시 홈으로 돌아갑니다",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
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
private fun NestedNavGraphSamplePreview() {
    MaterialTheme {
        NestedNavGraphSample()
    }
}
