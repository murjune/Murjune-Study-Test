package com.murjune.practice.plugins

import com.murjune.practice.plugins.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType

class UnitTestPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {

            tasks.withType<Test> {
                useJUnitPlatform()
            }

            dependencies {
                add("testImplementation", kotlin("test"))
                add("testImplementation", libs.findBundle("unit-test").get())
            }
        }
}
