package com.murjune.pratice.compose.study.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import androidx.navigation.toRoute
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * SavedStateHandle 최신 API를 검증하는 테스트
 *
 * 주요 API:
 * 1) getStateFlow() — key에 대한 읽기 전용 StateFlow
 * 2) getMutableStateFlow() — key에 대한 MutableStateFlow (읽기/쓰기)
 * 3) ViewModel + SavedStateHandle — Route 인자가 자동 주입
 * 4) savedStateHandle.toRoute<T>() — Route 객체로 직접 변환
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SavedStateHandleAdvancedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Serializable
    object AdvancedHome

    @Serializable
    data class UserRoute(
        val userId: String,
        val userName: String,
    )

    // ViewModel에서 SavedStateHandle 활용
    class UserViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
        // Route 인자를 toRoute로 한 번에 추출
        private val route = savedStateHandle.toRoute<UserRoute>()
        val userId: String = route.userId
        val userName: String = route.userName

        // getStateFlow — 읽기 전용 StateFlow
        val userIdFlow: StateFlow<String> = savedStateHandle.getStateFlow("userId", "")

        // getMutableStateFlow — 읽기/쓰기 MutableStateFlow
        val editableName = savedStateHandle.getMutableStateFlow("editableName", route.userName)
    }

    // =========================================================================
    // 1) getStateFlow — Route 인자를 StateFlow로 관찰
    // =========================================================================

    @Test
    fun `getStateFlow로_SavedStateHandle의_값을_StateFlow로_관찰할_수_있다`() {
        // given
        lateinit var navController: TestNavHostController
        var observedUserId: String? = null

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = AdvancedHome) {
                composable<AdvancedHome> { Text("Home") }
                composable<UserRoute> { backStackEntry ->
                    // getStateFlow로 관찰
                    val userIdFlow = backStackEntry.savedStateHandle
                        .getStateFlow("userId", "default")
                    val userId by userIdFlow.collectAsState()
                    observedUserId = userId
                    Text("Flow userId: $userId")
                }
            }
        }

        // when
        navController.navigate(UserRoute(userId = "flow-42", userName = "test"))

        // then
        composeTestRule.onNodeWithText("Flow userId: flow-42").assertIsDisplayed()
        observedUserId shouldBe "flow-42"
    }

    // =========================================================================
    // 2) getMutableStateFlow — 값을 수정하면 자동으로 반영
    // =========================================================================

    @Test
    fun `getMutableStateFlow로_값을_수정하면_UI에_반영된다`() {
        // given
        lateinit var navController: TestNavHostController

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = AdvancedHome) {
                composable<AdvancedHome> { Text("Home") }
                composable<UserRoute> { backStackEntry ->
                    val mutableFlow = backStackEntry.savedStateHandle
                        .getMutableStateFlow("counter", 0)
                    val count by mutableFlow.collectAsState()
                    Text("Count: $count")
                    androidx.compose.material3.Button(
                        onClick = { mutableFlow.value = count + 1 },
                    ) {
                        Text("increment")
                    }
                }
            }
        }

        navController.navigate(UserRoute(userId = "u1", userName = "test"))
        composeTestRule.onNodeWithText("Count: 0").assertIsDisplayed()

        // when - 버튼 클릭으로 MutableStateFlow 값 변경
        composeTestRule.onNodeWithText("increment").performClick()

        // then
        composeTestRule.onNodeWithText("Count: 1").assertIsDisplayed()
    }

    // =========================================================================
    // 3) ViewModel에서 SavedStateHandle.toRoute<T>()로 Route 인자 추출
    // =========================================================================

    @Test
    fun `ViewModel의_SavedStateHandle에_Route_인자가_자동_주입된다`() {
        // given
        lateinit var navController: TestNavHostController
        var viewModelUserId: String? = null
        var viewModelUserName: String? = null

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = AdvancedHome) {
                composable<AdvancedHome> { Text("Home") }
                composable<UserRoute> {
                    val vm: UserViewModel = viewModel()
                    viewModelUserId = vm.userId
                    viewModelUserName = vm.userName
                    Text("VM: ${vm.userId}, ${vm.userName}")
                }
            }
        }

        // when
        navController.navigate(UserRoute(userId = "vm-99", userName = "murjune"))

        // then - ViewModel에서 Route 인자를 정상 추출
        composeTestRule.onNodeWithText("VM: vm-99, murjune").assertIsDisplayed()
        viewModelUserId shouldBe "vm-99"
        viewModelUserName shouldBe "murjune"
    }

    // =========================================================================
    // 4) ViewModel의 getStateFlow가 Route 인자 변화를 반영
    // =========================================================================

    @Test
    fun `ViewModel의_getStateFlow가_Route_인자를_StateFlow로_제공한다`() {
        // given
        lateinit var navController: TestNavHostController
        var flowValue: String? = null

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = AdvancedHome) {
                composable<AdvancedHome> { Text("Home") }
                composable<UserRoute> {
                    val vm: UserViewModel = viewModel()
                    val userId by vm.userIdFlow.collectAsState()
                    flowValue = userId
                    Text("StateFlow: $userId")
                }
            }
        }

        // when
        navController.navigate(UserRoute(userId = "sf-77", userName = "test"))

        // then
        composeTestRule.onNodeWithText("StateFlow: sf-77").assertIsDisplayed()
        flowValue shouldBe "sf-77"
    }

    // =========================================================================
    // 5) SavedStateHandle의 keys(), contains(), remove()
    // =========================================================================

    @Test
    fun `SavedStateHandle의_유틸리티_메서드들이_정상_동작한다`() {
        // given
        lateinit var navController: TestNavHostController

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = AdvancedHome) {
                composable<AdvancedHome> { Text("Home") }
                composable<UserRoute> { Text("User") }
            }
        }

        navController.navigate(UserRoute(userId = "u1", userName = "murjune"))

        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        savedStateHandle shouldNotBe null

        // keys() — Route 인자의 키가 포함됨
        val keys = savedStateHandle!!.keys()
        keys.contains("userId") shouldBe true
        keys.contains("userName") shouldBe true

        // contains()
        savedStateHandle.contains("userId") shouldBe true
        savedStateHandle.contains("nonExistent") shouldBe false

        // get()
        savedStateHandle.get<String>("userId") shouldBe "u1"

        // set() + get()
        savedStateHandle["customKey"] = "customValue"
        savedStateHandle.get<String>("customKey") shouldBe "customValue"

        // remove()
        val removed = savedStateHandle.remove<String>("customKey")
        removed shouldBe "customValue"
        savedStateHandle.contains("customKey") shouldBe false
    }
}
