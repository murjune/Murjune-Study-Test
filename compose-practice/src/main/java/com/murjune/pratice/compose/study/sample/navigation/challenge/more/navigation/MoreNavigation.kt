package com.murjune.pratice.compose.study.sample.navigation.challenge.more.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.murjune.pratice.compose.study.sample.navigation.challenge.more.MoreScreen

fun NavController.navigateToMore(
    navOptions: NavOptions? = null,
) = navigate(MoreRoute, navOptions)

fun NavGraphBuilder.moreScreen(
    onBack: () -> Unit,
) {
    composable<MoreRoute> {
        MoreScreen(
            onBack = onBack,
        )
    }
}
