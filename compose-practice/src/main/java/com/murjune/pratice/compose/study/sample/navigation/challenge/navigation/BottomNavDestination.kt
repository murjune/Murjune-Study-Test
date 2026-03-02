package com.murjune.pratice.compose.study.sample.navigation.challenge.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.murjune.pratice.compose.study.R
import com.murjune.pratice.compose.study.sample.navigation.challenge.cart.navigation.CartNavGraph
import com.murjune.pratice.compose.study.sample.navigation.challenge.cart.navigation.CartRoute
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation.HomeNavGraph
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation.HomeRoute
import com.murjune.pratice.compose.study.sample.navigation.challenge.more.navigation.MoreRoute
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.navigation.MyNavGraph
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.navigation.MyRoute
import kotlin.reflect.KClass

enum class BottomNavDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
    val isSelectable: Boolean = true,
) {
    Home(
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        iconTextId = R.string.navigation_challenge_bottom_nav_home,
        titleTextId = R.string.navigation_challenge_bottom_nav_home,
        route = HomeRoute.HomeScreen::class,
        baseRoute = HomeNavGraph::class,
    ),
    Cart(
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart,
        iconTextId = R.string.navigation_challenge_bottom_nav_cart,
        titleTextId = R.string.navigation_challenge_bottom_nav_cart,
        route = CartRoute.CartScreen::class,
        baseRoute = CartNavGraph::class,
    ),
    My(
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        iconTextId = R.string.navigation_challenge_bottom_nav_my,
        titleTextId = R.string.navigation_challenge_bottom_nav_my,
        route = MyRoute.MyScreen::class,
        baseRoute = MyNavGraph::class,
    ),
    More(
        selectedIcon = Icons.Filled.MoreHoriz,
        unselectedIcon = Icons.Outlined.MoreHoriz,
        iconTextId = R.string.navigation_challenge_bottom_nav_more,
        titleTextId = R.string.navigation_challenge_bottom_nav_more,
        route = MoreRoute::class,
        isSelectable = false,
    );
}
