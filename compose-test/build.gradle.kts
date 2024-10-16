plugins {
    alias(libs.plugins.murjune.application)
    alias(libs.plugins.murjune.compose)
    alias(libs.plugins.murjune.compose.test)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.murjune.pratice.compose.study"

    packaging {
        resources {
            excludes += "META-INF/**"
            excludes += "win32-x86*/**"
        }
    }
}