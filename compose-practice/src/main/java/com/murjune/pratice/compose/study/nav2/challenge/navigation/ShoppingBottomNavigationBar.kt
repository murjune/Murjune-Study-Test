package com.murjune.pratice.compose.study.nav2.challenge.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlin.reflect.KClass

private val BottomNavHeight = 80.dp

fun Modifier.bottomNavPadding(): Modifier = padding(bottom = BottomNavHeight)

val BottomNavContentPadding = PaddingValues(bottom = BottomNavHeight)

@Composable
fun ShoppingBottomNavigationBar(
    destinations: List<BottomNavDestination>,
    currentDestination: NavDestination?,
    onNavigate: (BottomNavDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        destinations.forEach { destination ->
            val isSelected = destination.isSelectable &&
                currentDestination?.hierarchy?.any { it.hasRoute(destination.baseRoute) } == true
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(destination) },
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
}
