package com.murjune.pratice.compose.study.sample.navigation.challenge.setting.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.murjune.pratice.compose.study.sample.navigation.challenge.setting.SettingScreen

fun NavController.navigateToSetting(
    navOptions: NavOptions? = null,
) = navigate(SettingRoute, navOptions)

fun NavGraphBuilder.settingSection(
    onBack: () -> Unit,
) {
    composable<SettingRoute> {
        SettingScreen(
            onBack = onBack,
        )
    }
}
