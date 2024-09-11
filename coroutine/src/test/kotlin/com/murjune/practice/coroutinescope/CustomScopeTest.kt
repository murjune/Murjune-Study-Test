package com.murjune.practice.coroutinescope

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CustomScopeTest {
    @Test
    fun `CoroutineScope 가 자식 코루틴에게 컨택스트를 제공해주는 거다`() = runTest {
        // given
        val coroutineScope = object : CoroutineScope {
            override val coroutineContext =
                UnconfinedTestDispatcher() + Job()
        }

        coroutineScope.launch {
            coroutineContext[Job]?.parent shouldBe coroutineScope.coroutineContext[Job]
        }
    }
}