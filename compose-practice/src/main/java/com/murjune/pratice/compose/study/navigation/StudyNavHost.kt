package com.murjune.pratice.compose.study.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.murjune.pratice.compose.study.sample.navigation.DeepLinkSample
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
            NavHostBasicSample()
        }
        composable<TypeSafeNavRoute> {
            TypeSafeNavSample()
        }
        composable<PopBackStackRoute> {
            PopBackStackSample()
        }
        composable<PopUpToRoute> {
            PopUpToSample()
        }
        composable<DeepLinkRoute> {
            DeepLinkSample()
        }
    }
}
