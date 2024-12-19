plugins {
    `kotlin-dsl`
}

group = "com.murjune.practice.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.kotlin.gradleplugin)
    compileOnly(libs.agp)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        create("android-application") {
            id = "com.murjune.practice.application"
            implementationClass =
                "com.murjune.practice.plugins.AndroidApplicationPlugin"
        }
        create("android-library") {
            id = "com.murjune.practice.library"
            implementationClass = "com.murjune.practice.plugins.AndroidLibraryPlugin"
        }
        create("android-feature") {
            id = "com.murjune.practice.feature"
            implementationClass = "com.murjune.practice.plugins.AndroidFeaturePlugin"
        }
        create("android-hilt") {
            id = "com.murjune.practice.hilt"
            implementationClass = "com.murjune.practice.plugins.AndroidHiltPlugin"
        }
        create("android-test") {
            id = "com.murjune.practice.android.test"
            implementationClass = "com.murjune.practice.plugins.AndroidTestPlugin"
        }
        create("compose") {
            id = "com.murjune.practice.compose"
            implementationClass = "com.murjune.practice.plugins.ComposePlugin"
        }
        create("compose-test") {
            id = "com.murjune.practice.compose.test"
            implementationClass = "com.murjune.practice.plugins.ComposeTestPlugin"
        }
        create("kotlin-serialization") {
            id = "com.murjune.practice.kotlinx_serialization"
            implementationClass =
                "com.murjune.practice.plugins.KotlinSerializationPlugin"
        }

        register("kotlin-module") {
            id = "com.murjune.practice.kotlin.module"
            implementationClass = "com.murjune.practice.plugins.KotlinModulePlugin"
        }

        create("unit-test") {
            id = "com.murjune.practice.unit.test"
            implementationClass = "com.murjune.practice.plugins.UnitTestPlugin"
        }

        register("jvm-library") {
            id = "com.murjune.practice.jvm.library"
            implementationClass = "com.murjune.practice.plugins.JvmLibraryPlugin"
        }
    }
}
