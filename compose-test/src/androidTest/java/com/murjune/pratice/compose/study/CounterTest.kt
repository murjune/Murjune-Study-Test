package com.murjune.pratice.compose.study

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.murjune.pratice.compose.study.sample.Counter
import org.junit.Rule
import org.junit.Test

/*
* [onRoot] - 루트 노드를 찾습니다.
* [onNodeWithText] - 주어진 텍스트를 가진 노드를 찾습니다.
* [onNodeWithContentDescription] - 주어진 콘텐츠 설명을 가진 노드를 찾습니다. (e. Icon, Image)
* [onNodeWithTag] - 주어진 태그를 가진 노드를 찾습니다.
*
* ref: https://developer.android.com/develop/ui/compose/testing/testing-cheatsheet/
 */
class CounterTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun 텍스트로_노출되는지_확인() {
        // given
        val count = 0
        composeTestRule.setContent {
            Counter(count = count, onPlusCount = { }, onRefreshCount = { })
        }
        // when & then
        val text = "0"
        composeTestRule
            .onNodeWithText(text)
            .assertExists()
    }

    @Test
    fun contentDescription_에_해당하는_icon_찾기() {
        // given
        val count = 0
        composeTestRule.setContent {
            Counter(count = count, onPlusCount = { }, onRefreshCount = { })
        }
        // when & then
        val contextDescription = "Plus Count"
        composeTestRule
            .onNodeWithContentDescription(contextDescription)
            .assertIsDisplayed()
    }

    @Test
    fun test_tag_에_해당하는_icon_찾기() {
        // given
        val count = 0
        composeTestRule.setContent {
            Counter(count = count, onPlusCount = { }, onRefreshCount = { })
        }
        // when & then
        val tag = "Refresh"
        composeTestRule
            .onNodeWithTag(tag)
            .assertIsDisplayed()
    }

    @Test
    fun count_0일때_plus_누르면_1() {
        // given
        val count: MutableState<Int> = mutableStateOf(0)
        val onPlusCount: () -> Unit = { count.value++ }
        composeTestRule.setContent {
            Counter(count = count.value, onPlusCount = onPlusCount, onRefreshCount = {})
        }
        // when
        composeTestRule.onNodeWithContentDescription("Plus Count")
            .performClick()
        // then
        val expected = "1"
        composeTestRule.onNodeWithText(expected)
            .assertExists()
    }

    @Test
    fun count_3일때_refresh_누르면_0() {
        // given
        val count: MutableState<Int> = mutableStateOf(3)
        val onRefreshCount: () -> Unit = { count.value = 0 }
        composeTestRule.setContent {
            Counter(count = count.value, onPlusCount = {}, onRefreshCount = onRefreshCount)
        }
        // when
        composeTestRule.onNodeWithTag("Refresh")
            .performClick()
        // then
        val expected = "0"
        composeTestRule.onNodeWithText(expected)
            .isDisplayed()
    }
}