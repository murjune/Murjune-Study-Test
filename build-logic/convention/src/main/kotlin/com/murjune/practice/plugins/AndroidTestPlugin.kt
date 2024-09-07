package com.murjune.practice.plugins

import com.android.build.gradle.BaseExtension
import com.murjune.practice.plugins.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidTestPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply<UnitTestPlugin>()
            apply("de.mannodermaus.android-junit5")
        }

        extensions.getByType<BaseExtension>().apply {
            defaultConfig {
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                testInstrumentationRunnerArguments["runnerBuilder"] =
                    "de.mannodermaus.junit5.AndroidJUnit5Builder"
            }

            testOptions {
                unitTests {
                    isIncludeAndroidResources = true
                }
            }

            packagingOptions {
                resources.excludes.add("META-INF/LICENSE*")
            }
        }

        dependencies {
            add("androidTestImplementation", libs.findBundle("unit-test").get())
            add("testImplementation", libs.findLibrary("robolectric").get())
            add("androidTestImplementation", libs.findLibrary("junit5-android-test-core").get())
            add("androidTestRuntimeOnly", libs.findLibrary("junit5-android-test-runner").get())
            add("androidTestImplementation", libs.findBundle("androidx-android-test").get())
        }
    }
}
