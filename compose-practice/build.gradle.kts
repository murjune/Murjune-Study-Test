plugins {
    alias(libs.plugins.murjune.android.application)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.murjune.pratice.compose.study"

    defaultConfig {
        applicationId = "com.murjune.pratice.compose.study"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
