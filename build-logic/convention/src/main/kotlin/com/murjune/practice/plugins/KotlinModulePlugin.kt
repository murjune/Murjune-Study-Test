package com.murjune.practice.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * 순수 JVM + JUnit5 + Assertj
 * */
class KotlinModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply<JvmLibraryPlugin>()
            apply<JUnit5Plugin>()
        }

    }
}
