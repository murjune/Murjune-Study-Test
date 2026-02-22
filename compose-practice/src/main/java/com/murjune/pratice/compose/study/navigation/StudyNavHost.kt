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
                    navController.popBackStack()
                },
            )
        }
        composable<NavHostBasicRoute> {
            NavHostBasicSample(onBackClick = { navController.popBackStack() })
        }
        composable<TypeSafeNavRoute> {
            TypeSafeNavSample(onBackClick = { navController.popBackStack() })
        }
        composable<PopBackStackRoute> {
            PopBackStackSample(onBackClick = { navController.popBackStack() })
        }
        composable<PopUpToRoute> {
            PopUpToSample(onBackClick = { navController.popBackStack() })
        }
        composable<DeepLinkRoute> {
            DeepLinkSample(onBackClick = { navController.popBackStack() })
        }
        composable<LaunchSingleTopRoute> {
            LaunchSingleTopSample(onBackClick = { navController.popBackStack() })
        }
        composable<SavedStateHandleRoute> {
            SavedStateHandleSample(onBackClick = { navController.popBackStack() })
        }
        composable<SaveRestoreStateRoute> {
            SaveRestoreStateSample(onBackClick = { navController.popBackStack() })
        }
        composable<NestedNavGraphRoute> {
            NestedNavGraphSample(onBackClick = { navController.popBackStack() })
        }
        composable<NavigateUpRoute> {
            NavigateUpSample(onBackClick = { navController.popBackStack() })
        }
    }
}
