plugins {
    alias(libs.plugins.murjune.application)
    alias(libs.plugins.murjune.compose)
    alias(libs.plugins.murjune.android.test)
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

dependencies {
//    implementation(libs.koin.bom)
//    // Declare the koin dependencies that you need
//    implementation("io.insert-koin:koin-android")
//    implementation("io.insert-koin:koin-androidx-viewmodel")
//    testImplementation("io.insert-koin:koin-test")
    implementation(libs.appcompat.v7)
    // compose test
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}