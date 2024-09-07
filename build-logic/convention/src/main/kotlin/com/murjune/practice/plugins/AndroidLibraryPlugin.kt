package com.murjune.practice.plugins

import com.android.build.gradle.LibraryExtension
import com.murjune.practice.plugins.utils.configureAndroidCommonPlugin
import com.murjune.practice.plugins.utils.configureKotlinAndroid
import com.murjune.practice.plugins.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("kotlin-android")
            }
            configureAndroidCommonPlugin()

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk =
                    libs.findVersion("targetSdk").get().requiredVersion.toInt()
            }
        }
}
