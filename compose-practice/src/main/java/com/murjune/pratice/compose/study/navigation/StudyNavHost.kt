package com.murjune.pratice.compose.study.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.murjune.pratice.compose.study.sample.navigation.DeepLinkSample
import com.murjune.pratice.compose.study.sample.navigation.LaunchSingleTopSample
import com.murjune.pratice.compose.study.sample.navigation.NavHostBasicSample
import com.murjune.pratice.compose.study.sample.navigation.NavigateUpSample
import com.murjune.pratice.compose.study.sample.navigation.NestedNavGraphSample
import com.murjune.pratice.compose.study.sample.navigation.PopBackStackSample
import com.murjune.pratice.compose.study.sample.navigation.PopUpToSample
import com.murjune.pratice.compose.study.sample.navigation.SaveRestoreStateSample
import com.murjune.pratice.compose.study.sample.navigation.SavedStateHandleSample
import com.murjune.pratice.compose.study.sample.navigation.TypeSafeNavSample
import com.murjune.pratice.compose.study.sample.navigation.challenge.ShoppingApp
import com.murjune.pratice.compose.study.sample.navigation.challenge.rememberShoppingAppState
import com.murjune.pratice.compose.study.screen.StudyMainScreen
import com.murjune.pratice.compose.study.screen.TopicScreen

@Composable
fun StudyNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = StudyMainRoute,
    ) {
        composable<StudyMainRoute> {
            StudyMainScreen(
                onTopicClick = { topicId, topicTitle ->
                    navController.navigate(
                        TopicRoute(
                            topicId = topicId,
                            topicTitle = topicTitle,
                        ),
                    )
                },
            )
        }
        composable<TopicRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<TopicRoute>()
            TopicScreen(
                topicId = route.topicId,
                topicTitle = route.topicTitle,
                onSampleClick = { sampleRoute ->
                    navController.navigate(sampleRoute)
                },
                onBack = {
                    navController.navigateUp()
                },
            )
        }
        navigation<NavigationGraphRoute>(
            startDestination = NavHostBasicRoute,
        ) {
            composable<ShoppingChallengeRoute> {
                ShoppingApp(appState = rememberShoppingAppState())
            }
            composable<NavHostBasicRoute> {
                NavHostBasicSample(onBack = { navController.navigateUp() })
            }
            composable<TypeSafeNavRoute> {
                TypeSafeNavSample(onBack = { navController.navigateUp() })
            }
            composable<PopBackStackRoute> {
                PopBackStackSample(onBack = { navController.navigateUp() })
            }
            composable<PopUpToRoute> {
                PopUpToSample(onBack = { navController.navigateUp() })
            }
            composable<DeepLinkRoute> {
                DeepLinkSample(onBack = { navController.navigateUp() })
            }
            composable<LaunchSingleTopRoute> {
                LaunchSingleTopSample(onBack = { navController.navigateUp() })
            }
            composable<SavedStateHandleRoute> {
                SavedStateHandleSample(onBack = { navController.navigateUp() })
            }
            composable<SaveRestoreStateRoute> {
                SaveRestoreStateSample(onBack = { navController.navigateUp() })
            }
            composable<NestedNavGraphRoute> {
                NestedNavGraphSample(onBack = { navController.navigateUp() })
            }
            composable<NavigateUpRoute> {
                NavigateUpSample(onBack = { navController.navigateUp() })
            }
        }
    }
}
