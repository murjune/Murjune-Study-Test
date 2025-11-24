package com.murjune.pratice.compose.study.stability

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.tooling.preview.Preview
import com.murjune.pratice.compose.study.sample.Counter
import org.junit.Rule
import org.junit.Test
import java.lang.System.currentTimeMillis

class StabilityTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun 텍스트로_노출되는지_확인() {
        // given
        var count = 0
        val tag = "counter"
        val contextDescription = "Plus Count"

        composeTestRule.setContent {
            Counter(
                modifier = Modifier.testTag(tag),
                count = count,
                onPlusCount = { count++ },
                onRefreshCount = {}
            )
        }
        // when
        composeTestRule
            .onNodeWithContentDescription(contextDescription)
            .performClick()

        // then
        val text = "0"
        composeTestRule
            .onNodeWithText(text)
            .assertExists()
    }

    @Test
    fun test_NoInline_Composable() {
        // given
        val tag = "counter"
        val contextDescription = "Plus Count"
        var currentTime: String = time()
        composeTestRule.setContent {
            var count by remember { mutableStateOf(0) }

            Column {
                Counter(
                    modifier = Modifier.testTag(tag),
                    count = count,
                    onPlusCount = { count++ },
                    onRefreshCount = {}
                )

                Text(text = time().also { currentTime = it })
            }
        }

        // when
        val firstTime = currentTime

        composeTestRule
            .onNodeWithContentDescription(contextDescription)
            .performClick()

        val secondTime = currentTime

        // then
        assert(firstTime == secondTime)
    }

    @Composable
    inline fun InlineComposable(text: Any) {

    }


    @Composable
    inline fun InlineComposable2(
        text: String,
        crossinline onComposed: () -> Unit
    ) {
        SideEffect { onComposed() }
        Text(text = text)
    }
    @Test
    fun test_Inline_Composable() {
        // given
        val times = mutableListOf<Long>()

        composeTestRule.setContent {
            var count by remember { mutableStateOf(0) }

            Column {
                Counter(
                    modifier = Modifier.testTag("counter"),
                    count = count,
                    onPlusCount = { count++ },
                    onRefreshCount = {}
                )

                InlineComposable2(time()) {
                    // InlineComposable이 호출될 때마다 실행
                    times.add(currentTimeMillis())
                }
            }
        }

        // when
        val initialCallCount = times.size

        composeTestRule
            .onNodeWithContentDescription("Plus Count")
            .performClick()

        // then
        assert(times.size > initialCallCount)  // 재호출됨
    }

    private fun time() = currentTimeMillis().toString()
}

@Composable
@Preview
fun StabilityTestPreview() {
    var count by remember { mutableStateOf(0) }

    Column(modifier = Modifier.systemBarsPadding()) {
        Counter(
            modifier = Modifier,
            count = count,
            onPlusCount = { count++ },
            onRefreshCount = {}
        )

        InlineComposable(time())
    }
}

fun time() = currentTimeMillis().toString()

@Composable
inline fun InlineComposable(text: (String)) {
    Text(text = text)
}