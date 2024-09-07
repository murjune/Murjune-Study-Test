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
    implementation(libs.bundles.retrofit)
    implementation(libs.lifecycle)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3.compose)
    implementation(libs.appcompat.v7)
    implementation(libs.design)
    implementation(libs.support.annotations)
    implementation(libs.constraint.layout)
    implementation(libs.livedata)
    implementation(libs.viewmodel)
    // compose test
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}