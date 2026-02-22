package com.murjune.pratice.compose.study.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.murjune.pratice.compose.study.sample.navigation.DeepLinkSample
import com.murjune.pratice.compose.study.sample.navigation.LaunchSingleTopSample
import com.murjune.pratice.compose.study.sample.navigation.NavigateUpSample
import com.murjune.pratice.compose.study.sample.navigation.NestedNavGraphSample
import com.murjune.pratice.compose.study.sample.navigation.SaveRestoreStateSample
import com.murjune.pratice.compose.study.sample.navigation.SavedStateHandleSample
import com.murjune.pratice.compose.study.sample.navigation.NavHostBasicSample
import com.murjune.pratice.compose.study.sample.navigation.PopBackStackSample
import com.murjune.pratice.compose.study.sample.navigation.PopUpToSample
import com.murjune.pratice.compose.study.sample.navigation.TypeSafeNavSample
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
                onBackClick = {
                    navController.navigateUp()
                },
            )
        }
        composable<NavHostBasicRoute> {
            NavHostBasicSample(onBackClick = { navController.navigateUp() })
        }
        composable<TypeSafeNavRoute> {
            TypeSafeNavSample(onBackClick = { navController.navigateUp() })
        }
        composable<PopBackStackRoute> {
            PopBackStackSample(onBackClick = { navController.navigateUp() })
        }
        composable<PopUpToRoute> {
            PopUpToSample(onBackClick = { navController.navigateUp() })
        }
        composable<DeepLinkRoute> {
            DeepLinkSample(onBackClick = { navController.navigateUp() })
        }
        composable<LaunchSingleTopRoute> {
            LaunchSingleTopSample(onBackClick = { navController.navigateUp() })
        }
        composable<SavedStateHandleRoute> {
            SavedStateHandleSample(onBackClick = { navController.navigateUp() })
        }
        composable<SaveRestoreStateRoute> {
            SaveRestoreStateSample(onBackClick = { navController.navigateUp() })
        }
        composable<NestedNavGraphRoute> {
            NestedNavGraphSample(onBackClick = { navController.navigateUp() })
        }
        composable<NavigateUpRoute> {
            NavigateUpSample(onBackClick = { navController.navigateUp() })
        }
    }
}
