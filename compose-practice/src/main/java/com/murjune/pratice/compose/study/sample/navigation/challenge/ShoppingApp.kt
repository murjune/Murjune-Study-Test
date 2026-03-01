package com.murjune.pratice.compose.study.sample.navigation.challenge

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation.HomeRoute
import kotlin.reflect.KClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingApp(
    appState: ShoppingAppState,
    modifier: Modifier = Modifier,
) {
    val currentDestination = appState.currentDestination
    val shouldShowBottomNav = appState.topLevelDestinations.any { currentDestination.isRouteInHierarchy(it.route) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (shouldShowBottomNav.not()) return@Scaffold

            NavigationBar {
                appState.topLevelDestinations.forEach { destination ->
                    val isSelected = currentDestination.isRouteInHierarchy(destination.route)
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            appState.navigateToTopLevelDestination(destination)
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = stringResource(destination.iconTextId),
                            )
                        },
                        label = { Text(stringResource(destination.titleTextId)) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = appState.navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding),
        ) {

        }
    }
}

@Composable
private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route)
    } ?: false