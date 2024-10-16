package com.murjune.pratice.compose.study

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.onIdle
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * - state 를 가지고 있는 컴포넌트는 state 가 변경될 때마다 recomposition 이 일어난다.
 * 특정 컴포넌트에서만 리컴포지션이 일어나길 기대하고 있지만, 부모 컴포넌트에서 props 에 State 값을 읽고 전달하고 있기 때문에
 * 부모도 리컴포지션이 일어난다.
 * 따라서, lazy 하게 값을 읽을 수 있도록 람다를 활용하여 값을 전달하여 리컴포지션 최적화를 할 수 있다.
 *
 * ref : https://www.jetpackcompose.app/articles/donut-hole-skipping-in-jetpack-compose
 * */
class DonutHoleSkippingTest {

    @get:Rule
    val composeRule = createComposeRule()
    var parentCompositionCnt = 0
    var childCompositionCnt = 0

    @Before
    fun setUp() {
        parentCompositionCnt = 0
        childCompositionCnt = 0
    }

    @Composable
    private fun RecomposedParent(
        cnt: Int,
    ) {
        Child2(cnt)
    }

    @Composable
    private fun Child2(cnt: Int) {
        Text(text = "$cnt").also { childCompositionCnt++ }
    }


    @Composable
    private fun NoRecomposedParent(
        cntProvider: () -> Int
    ) {
        Child(cntProvider)
    }

    @Composable
    private fun <T> Child(cntProvider: () -> T) {
        Text(text = "${cntProvider()}").also { childCompositionCnt++ }
    }

    @Test
    fun 부모와_자식이_recomposition_발생() {
        // given
        composeRule.setContent {
            var cnt by remember {
                mutableStateOf(1)
            }
            Button(onClick = { cnt++ }) {}
            RecomposedParent(cnt = cnt).also { parentCompositionCnt++ }
        }
        // 최초의 컴포지션에 + 1 씩
        assert(parentCompositionCnt == 1)
        assert(childCompositionCnt == 1)
        // when : 버튼을 3번 클릭
        composeRule.onNode(
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        ).performClick().performClick().performClick()
        onIdle()
        // then
        assert(parentCompositionCnt == 4)
        assert(childCompositionCnt == 4)
    }

    @Test
    fun 자식만_recomposition이_일어난다() {
        // given
        composeRule.setContent {
            var cnt by remember {
                mutableStateOf(1)
            }

            Button(onClick = { cnt++ }) {}
            NoRecomposedParent(cntProvider = { cnt }).also { parentCompositionCnt++ }
        }
        // 최초의 컴포지션에 + 1 씩
        assert(parentCompositionCnt == 1)
        assert(childCompositionCnt == 1)
        // when : 버튼을 3번 클릭
        composeRule.onNode(
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        ).performClick().performClick().performClick()
        onIdle()
        // then
        assert(parentCompositionCnt == 1)
        assert(childCompositionCnt == 4)
    }
}