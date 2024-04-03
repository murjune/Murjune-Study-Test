package com.murjune.practice.plugins

import com.murjune.practice.plugins.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType

class JUnit5Plugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {

            tasks.withType<Test> {
                useJUnitPlatform()
            }

            dependencies {
                add("testImplementation", kotlin("test"))
                add("testImplementation", libs.findBundle("junit5").get())
                add("testImplementation", libs.findLibrary("assertj").get())
                add("testImplementation", libs.findLibrary("kotlin-coroutines-test").get())
                add("testImplementation", libs.findLibrary("kotest").get())
            }
        }
}
