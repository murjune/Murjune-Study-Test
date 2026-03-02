package com.murjune.pratice.compose.study.sample.navigation.challenge

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.murjune.pratice.compose.study.R
import com.murjune.pratice.compose.study.sample.navigation.challenge.cart.navigation.CartBaseRoute
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation.HomeBaseRoute
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.navigation.MyBaseRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
) {
    Home(
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        iconTextId = R.string.navigation_challenge_bottom_nav_home,
        titleTextId = R.string.navigation_challenge_bottom_nav_home,
        route = HomeBaseRoute::class,
    ),
    Cart(
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart,
        iconTextId = R.string.navigation_challenge_bottom_nav_cart,
        titleTextId = R.string.navigation_challenge_bottom_nav_cart,
        route = CartBaseRoute::class,
    ),
    My(
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        iconTextId = R.string.navigation_challenge_bottom_nav_my,
        titleTextId = R.string.navigation_challenge_bottom_nav_my,
        route = MyBaseRoute::class,
    );
}
