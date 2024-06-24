package com.murjune.practice.utils

import kotlinx.coroutines.Job

fun Job.shouldBeActive() {
    require(isActive) { "Expected to be active, but is Not active" }
}

fun Job.shouldBeCompleted() {
    require(isCompleted) { "Expected to be completed, but is Not completed" }
}

fun Job.shouldBeCancelled() {
    require(isCancelled) { "Expected to be cancelled, but is Not cancelled" }
}
