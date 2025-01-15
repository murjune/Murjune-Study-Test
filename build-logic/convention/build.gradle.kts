import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.murjune.practice.buildlogic"

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}

gradlePlugin {
    plugins {
        create("android-application") {
            id = libs.plugins.murjune.android.application.get().pluginId
            implementationClass =
                "AndroidApplicationPlugin"
        }
        create("android-feature") {
            id = libs.plugins.murjune.android.feature.get().pluginId
            implementationClass = "AndroidFeaturePlugin"
        }
        create("android-library") {
            id = libs.plugins.murjune.android.library.get().pluginId
            implementationClass = "AndroidLibraryPlugin"
        }
        create("android-compose") {
            id = libs.plugins.murjune.android.compose.get().pluginId
            implementationClass = "ComposePlugin"
        }
        create("kotlinx-serialization") {
            id = libs.plugins.murjune.kotlinx.serialization.get().pluginId
            implementationClass =
                "KotlinSerializationPlugin"
        }
        create("unit-test") {
            id = libs.plugins.murjune.unit.test.get().pluginId
            implementationClass = "UnitTestPlugin"
        }

        register("jvm-library") {
            id = libs.plugins.murjune.jvm.library.get().pluginId
            implementationClass = "JvmLibraryPlugin"
        }
    }
}
