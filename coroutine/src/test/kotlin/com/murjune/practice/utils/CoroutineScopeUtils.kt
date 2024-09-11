package com.murjune.practice.utils

import io.kotest.assertions.throwables.shouldThrow
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest

inline fun <reified T : Exception> runErrorTest(crossinline block: suspend TestScope.() -> Unit): TestResult {
    shouldThrow<T> {
        runTest {
            block(this)
        }
    }
}
