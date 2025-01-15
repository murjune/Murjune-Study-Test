package com.murjune.practice.plugins

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension


/**
 * Configure Compose-specific options
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures.apply {
            compose = true
        }
        testOptions {
            animationsDisabled = true
            unitTests {
                // For Robolectric
                isIncludeAndroidResources = true
            }
        }

        dependencies {
            val bom = libs.findLibrary("androidx.compose.bom").get()
            "implementation"(platform(bom))
            "implementation"(libs.findBundle("compose").get())
            "implementation"(libs.findLibrary("coil.kt.compose").get())
            "testImplementation"(platform(bom))
            "testImplementation"(libs.findLibrary("robolectric").get())
            "testImplementation"(libs.findLibrary("androidx.compose.ui.test.junit4").get())
            "androidTestImplementation"(platform(bom))
            "androidTestImplementation"(libs.findLibrary("androidx.compose.ui.test.junit4").get())
            "debugImplementation"(libs.findLibrary("androidx.compose.ui.tooling").get())
            "debugImplementation"(libs.findLibrary("androidx.compose.ui.test.manifest").get())
        }
    }

    extensions.configure<ComposeCompilerGradlePluginExtension> {
        includeSourceInformation = true
//        stabilityConfigurationFiles
//            .add(isolated.rootProject.projectDirectory.file("compose_compiler_config.conf"))
    }
}
