package com.murjune.practice.plugins

import com.android.build.gradle.BaseExtension
import com.murjune.practice.plugins.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class ComposePlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            extensions.getByType<BaseExtension>().apply {
                buildFeatures.apply {
                    compose = true
                }
                composeOptions {
                    kotlinCompilerExtensionVersion =
                        libs.findVersion("composeCompiler").get().toString()
                }
            }

            dependencies {
                add("implementation", platform(libs.findLibrary("compose.bom").get()))
                add("implementation", libs.findBundle("compose").get())
            }
        }
}
