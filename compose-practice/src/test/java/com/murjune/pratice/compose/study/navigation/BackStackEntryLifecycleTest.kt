package com.murjune.pratice.compose.study.navigation

import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.Serializable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * NavBackStackEntry의 Lifecycle 관리를 검증하는 학습 테스트
 *
 * 각 NavBackStackEntry는 독립적인 LifecycleOwner이다:
 * - 백스택 최상단(top)만 RESUMED 상태
 * - popBackStack으로 제거되면 DESTROYED
 * - 같은 destination이어도 엔트리가 다르면 별도 Lifecycle
 */
@RunWith(RobolectricTestRunner::class)
class BackStackEntryLifecycleTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Serializable
    object Home

    @Serializable
    data class Detail(
        val id: Int,
    )

    @Serializable
    object Settings

    private fun setupNavHost(): TestNavHostController {
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Home) {
                composable<Home> { Text("Home") }
                composable<Detail> { Text("Detail") }
                composable<Settings> { Text("Settings") }
            }
        }
        composeTestRule.waitForIdle()
        return navController
    }

    private fun navigateAndIdle(
        navController: TestNavHostController,
        route: Any,
    ): NavBackStackEntry {
        composeTestRule.runOnIdle {
            navController.navigate(route)
        }
        composeTestRule.waitForIdle()
        return navController.currentBackStackEntry!!
    }

    private fun popAndIdle(navController: TestNavHostController): Boolean {
        var result = false
        composeTestRule.runOnIdle {
            result = navController.popBackStack()
        }
        composeTestRule.waitForIdle()
        return result
    }

    // =========================================================================
    // 1) 각 BackStackEntry는 독립적인 LifecycleOwner이다
    // =========================================================================

    @Test
    fun `각_BackStackEntry는_독립적인_Lifecycle을_가진다`() {
        // given
        val navController = setupNavHost()
        val homeEntry = navController.currentBackStackEntry!!

        // when
        val detailEntry = navigateAndIdle(navController, Detail(id = 1))

        // then - Home과 Detail은 서로 다른 LifecycleOwner
        homeEntry shouldNotBe detailEntry
        homeEntry.lifecycle shouldNotBe detailEntry.lifecycle
    }

    // =========================================================================
    // 2) 백스택 최상단만 RESUMED
    // =========================================================================

    @Test
    fun `백스택_최상단_엔트리만_RESUMED_상태이다`() {
        // given
        val navController = setupNavHost()

        // Home만 있을 때 — Home이 RESUMED
        composeTestRule.runOnIdle {
            navController.currentBackStackEntry!!
                .lifecycle.currentState shouldBe Lifecycle.State.RESUMED
        }

        // when - Detail로 이동
        val detailEntry = navigateAndIdle(navController, Detail(id = 1))

        // then - Detail이 RESUMED
        composeTestRule.runOnIdle {
            detailEntry.lifecycle.currentState shouldBe Lifecycle.State.RESUMED
        }
    }

    // =========================================================================
    // 3) popBackStack 시 제거되는 엔트리는 DESTROYED
    // =========================================================================

    @Test
    fun `popBackStack으로_제거된_엔트리는_DESTROYED_된다`() {
        // given
        val navController = setupNavHost()
        val detailEntry = navigateAndIdle(navController, Detail(id = 1))

        composeTestRule.runOnIdle {
            detailEntry.lifecycle.currentState shouldBe Lifecycle.State.RESUMED
        }

        // when - Detail을 pop
        popAndIdle(navController)

        // then - Detail 엔트리는 DESTROYED
        composeTestRule.runOnIdle {
            detailEntry.lifecycle.currentState shouldBe Lifecycle.State.DESTROYED
            // Home은 다시 RESUMED
            navController.currentBackStackEntry!!
                .lifecycle.currentState shouldBe Lifecycle.State.RESUMED
        }
    }

    // =========================================================================
    // 4) 같은 destination이어도 엔트리가 다르면 별도 Lifecycle
    // =========================================================================

    @Test
    fun `같은_destination이어도_별도_엔트리이므로_Lifecycle이_독립적이다`() {
        // given
        val navController = setupNavHost()

        // Detail(1) → Detail(2) 두 번 navigate (launchSingleTop 없이)
        val detail1Entry = navigateAndIdle(navController, Detail(id = 1))
        val detail2Entry = navigateAndIdle(navController, Detail(id = 2))

        // then - 같은 destination이지만 서로 다른 엔트리
        detail1Entry shouldNotBe detail2Entry

        composeTestRule.runOnIdle {
            detail2Entry.lifecycle.currentState shouldBe Lifecycle.State.RESUMED
        }

        // when - Detail(2) pop
        popAndIdle(navController)

        // then - Detail(2)는 DESTROYED, Detail(1)은 다시 RESUMED
        composeTestRule.runOnIdle {
            detail2Entry.lifecycle.currentState shouldBe Lifecycle.State.DESTROYED
            detail1Entry.lifecycle.currentState shouldBe Lifecycle.State.RESUMED
        }
    }

    // =========================================================================
    // 5) navigate → pop → navigate 전체 Lifecycle 전이 확인
    // =========================================================================

    @Test
    fun `navigate와_pop에_따른_Lifecycle_상태_전이를_확인한다`() {
        // given
        val navController = setupNavHost()
        val homeEntry = navController.currentBackStackEntry!!

        composeTestRule.runOnIdle {
            homeEntry.lifecycle.currentState shouldBe Lifecycle.State.RESUMED
        }

        // when - Home → Detail → Settings
        val detailEntry = navigateAndIdle(navController, Detail(id = 1))
        val settingsEntry = navigateAndIdle(navController, Settings)

        // then - 최상단만 RESUMED
        composeTestRule.runOnIdle {
            settingsEntry.lifecycle.currentState shouldBe Lifecycle.State.RESUMED
        }

        // when - Settings pop → Detail이 다시 RESUMED
        popAndIdle(navController)

        composeTestRule.runOnIdle {
            settingsEntry.lifecycle.currentState shouldBe Lifecycle.State.DESTROYED
            detailEntry.lifecycle.currentState shouldBe Lifecycle.State.RESUMED
        }

        // when - Detail pop → Home이 다시 RESUMED
        popAndIdle(navController)

        composeTestRule.runOnIdle {
            detailEntry.lifecycle.currentState shouldBe Lifecycle.State.DESTROYED
            homeEntry.lifecycle.currentState shouldBe Lifecycle.State.RESUMED
        }
    }

    // =========================================================================
    // 6) popUpTo로 여러 엔트리가 한번에 제거되면 모두 DESTROYED
    // =========================================================================

    @Test
    fun `popUpTo로_한번에_제거된_엔트리들은_모두_DESTROYED_된다`() {
        // given
        val navController = setupNavHost()

        // Home → Detail → Settings
        val detailEntry = navigateAndIdle(navController, Detail(id = 1))
        val settingsEntry = navigateAndIdle(navController, Settings)

        // when - Home까지 popUpTo (Detail, Settings 둘 다 제거)
        composeTestRule.runOnIdle {
            navController.navigate(Home) {
                popUpTo<Home> {  }
            }
        }
        composeTestRule.waitForIdle()

        // then - Detail과 Settings 모두 DESTROYED
        composeTestRule.runOnIdle {
            detailEntry.lifecycle.currentState shouldBe Lifecycle.State.DESTROYED
            settingsEntry.lifecycle.currentState shouldBe Lifecycle.State.DESTROYED
        }
    }

    // =========================================================================
    // 7) 각 엔트리는 독립적인 SavedStateHandle + Lifecycle
    // =========================================================================

    @Test
    fun `각_엔트리는_독립적인_SavedStateHandle과_Lifecycle을_가진다`() {
        // given
        val navController = setupNavHost()

        // Home 엔트리에 값 설정
        val homeEntry = navController.currentBackStackEntry!!
        composeTestRule.runOnIdle {
            homeEntry.savedStateHandle["myKey"] = "homeValue"
        }

        // Detail로 이동
        val detailEntry = navigateAndIdle(navController, Detail(id = 1))
        composeTestRule.runOnIdle {
            detailEntry.savedStateHandle["myKey"] = "detailValue"
        }

        // then - 각 엔트리의 SavedStateHandle은 독립적
        composeTestRule.runOnIdle {
            homeEntry.savedStateHandle.get<String>("myKey") shouldBe "homeValue"
            detailEntry.savedStateHandle.get<String>("myKey") shouldBe "detailValue"

            // Lifecycle도 독립적
            detailEntry.lifecycle.currentState shouldBe Lifecycle.State.RESUMED
            homeEntry.lifecycle.currentState
                .isAtLeast(Lifecycle.State.CREATED) shouldBe true
        }
    }
}
